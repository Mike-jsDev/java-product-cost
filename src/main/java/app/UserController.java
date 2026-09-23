package app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request.getName(), request.getEmail());
    }

    @GetMapping("/by-name")
    public List<User> getUsersByName(@RequestParam String name) {
        return userService.getUsersByName(name);
    }

    @GetMapping("/by-email-domain")
    public List<User> getUsersByEmailDomain(@RequestParam String domain) {
        return userService.getUsersByEmailDomain(domain);
    }

    @GetMapping("/{userId}/posts")
    public List<Post> getPostsByUserId(@PathVariable Long userId) {
        return userService.getPostsByUserId(userId);
    }

    @PostMapping("/with-post")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUserWithPost(@RequestBody CreateUserWithPostRequest request) {
        return userService.createUserWithPost(
                request.getName(),
                request.getEmail(),
                request.getTitle(),
                request.getContent(),
                request.isFail());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleRollback(IllegalStateException ex) {
        return Map.of(
                "rolledBack", true,
                "message", ex.getMessage());
    }
}
