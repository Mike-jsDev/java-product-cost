(() => {
  const displayEl = document.getElementById("display");
  const expressionEl = document.getElementById("expression");

  let current = "0";
  let expression = "";
  let shouldResetCurrent = false;
  let lastResult = null;

  function updateDisplay() {
    displayEl.textContent = current;
    expressionEl.textContent = expression;
    displayEl.classList.toggle("is-error", current.startsWith("Error"));
  }

  function clearAll() {
    current = "0";
    expression = "";
    shouldResetCurrent = false;
    lastResult = null;
    updateDisplay();
  }

  function backspace() {
    if (current.startsWith("Error") || shouldResetCurrent) {
      current = "0";
      shouldResetCurrent = false;
      updateDisplay();
      return;
    }

    if (current.length <= 1 || (current.length === 2 && current.startsWith("-"))) {
      current = "0";
    } else {
      current = current.slice(0, -1);
    }
    updateDisplay();
  }

  function appendDigit(digit) {
    if (current.startsWith("Error")) {
      clearAll();
    }

    if (shouldResetCurrent || current === "0") {
      current = digit;
      shouldResetCurrent = false;
    } else {
      current += digit;
    }
    updateDisplay();
  }

  function appendDot() {
    if (current.startsWith("Error")) {
      clearAll();
    }

    if (shouldResetCurrent) {
      current = "0.";
      shouldResetCurrent = false;
      updateDisplay();
      return;
    }

    if (!current.includes(".")) {
      current += ".";
      updateDisplay();
    }
  }

  function setOperator(operator) {
    if (current.startsWith("Error")) {
      return;
    }

    if (expression && !shouldResetCurrent) {
      calculate(false);
      if (current.startsWith("Error")) {
        return;
      }
    }

    expression = `${current} ${operator}`;
    shouldResetCurrent = true;
    lastResult = null;
    updateDisplay();
  }

  function formatResult(value) {
    if (!Number.isFinite(value)) {
      return "Error";
    }

    const rounded = Math.round((value + Number.EPSILON) * 1e10) / 1e10;
    return String(rounded);
  }

  function calculate(finalize) {
    if (current.startsWith("Error")) {
      return;
    }

    if (!expression) {
      if (finalize) {
        expression = `${current} =`;
        lastResult = Number(current);
        updateDisplay();
      }
      return;
    }

    const parts = expression.trim().split(" ");
    if (parts.length < 2) {
      return;
    }

    const left = Number(parts[0]);
    const operator = parts[1];
    const right = Number(current);

    if (Number.isNaN(left) || Number.isNaN(right)) {
      current = "Error: invalid input";
      expression = "";
      shouldResetCurrent = true;
      updateDisplay();
      return;
    }

    let result;
    switch (operator) {
      case "+":
        result = left + right;
        break;
      case "-":
        result = left - right;
        break;
      case "*":
        result = left * right;
        break;
      case "/":
        if (right === 0) {
          current = "Error: division by zero";
          expression = "";
          shouldResetCurrent = true;
          lastResult = null;
          updateDisplay();
          return;
        }
        result = left / right;
        break;
      default:
        current = "Error: unknown operation";
        expression = "";
        shouldResetCurrent = true;
        updateDisplay();
        return;
    }

    const formatted = formatResult(result);
    if (finalize) {
      expression = `${left} ${operator} ${right} =`;
      current = formatted;
      shouldResetCurrent = true;
      lastResult = result;
    } else {
      current = formatted;
      shouldResetCurrent = true;
    }
    updateDisplay();
  }

  document.querySelector(".keys").addEventListener("click", (event) => {
    const button = event.target.closest("button");
    if (!button) {
      return;
    }

    if (button.dataset.digit !== undefined) {
      appendDigit(button.dataset.digit);
      return;
    }

    if (button.dataset.operator) {
      setOperator(button.dataset.operator);
      return;
    }

    switch (button.dataset.action) {
      case "clear":
        clearAll();
        break;
      case "backspace":
        backspace();
        break;
      case "dot":
        appendDot();
        break;
      case "equals":
        calculate(true);
        break;
      default:
        break;
    }
  });

  document.addEventListener("keydown", (event) => {
    const { key } = event;

    if (/^\d$/.test(key)) {
      appendDigit(key);
      return;
    }

    if (key === ".") {
      appendDot();
      return;
    }

    if (key === "+" || key === "-" || key === "*" || key === "/") {
      setOperator(key);
      return;
    }

    if (key === "Enter" || key === "=") {
      event.preventDefault();
      calculate(true);
      return;
    }

    if (key === "Backspace") {
      backspace();
      return;
    }

    if (key === "Escape" || key.toLowerCase() === "c") {
      clearAll();
    }
  });

  updateDisplay();
})();
