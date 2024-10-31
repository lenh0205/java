package com.example.demo.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// the JpaRepository need to specify the "entity datatype" we want to work with
// and the "ID datatype of the entity"
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
