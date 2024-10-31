# Layer

## API Layer

```java
@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired // for DI - saying that "StudentService" should be autowired 
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getStudents() {
        return studentService.getStudents();
    }
}
```

## Service Layer
* -> chứa business logic

```java
@Service // mark this as spring bean
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }
}
```

## Data Access Layer

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```