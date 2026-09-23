package app;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public User createUser(String name, String email) {
        validateUser(name, email);
        return userRepository.save(new User(name, email));
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByEmailDomain(String domain) {
        String normalized = normalizeDomain(domain);
        return userRepository.findByEmailEndingWith(normalized);
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " was not found");
        }
        return postRepository.findByUserId(userId);
    }

    /**
     * Creates a user and an initial post in one transaction.
     * If {@code fail} is true, throws after both saves are prepared so the whole transaction rolls back.
     */
    @Transactional
    public User createUserWithPost(String name, String email, String title, String content, boolean fail) {
        validateUser(name, email);
        validatePost(title, content);

        User user = userRepository.save(new User(name, email));

        Post post = new Post(title, content);
        user.addPost(post);
        postRepository.save(post);

        if (fail) {
            throw new IllegalStateException("Simulated failure — transaction must roll back");
        }
        return user;
    }

    private void validateUser(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name must not be blank");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email must be valid");
        }
    }

    private void validatePost(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post title must not be blank");
        }
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post content must not be blank");
        }
    }

    private String normalizeDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email domain must not be blank");
        }
        return domain.startsWith("@") ? domain : "@" + domain;
    }
}
