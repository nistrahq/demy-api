package com.nistra.demy.platform.scheduling.application.internal.queryservices;

import com.nistra.demy.platform.scheduling.domain.model.aggregates.Classroom;
import com.nistra.demy.platform.scheduling.domain.model.queries.GetAllClassroomsQuery;
import com.nistra.demy.platform.scheduling.domain.model.queries.GetClassroomByIdQuery;
import com.nistra.demy.platform.scheduling.domain.services.ClassroomQueryService;
import com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories.ClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Classroom Query Service Implementation
 * <p>This class implements the classroom query service interface and provides the business logic for handling classroom queries such as retrieving all classrooms and finding classrooms by ID.</p>
 */
@Service
public class ClassroomQueryServiceImpl implements ClassroomQueryService {

    private final ClassroomRepository classroomRepository;

    /**
     * Constructor that initializes the service with the required repository.
     * @param classroomRepository The classroom repository.
     */
    public ClassroomQueryServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    /**
     * This method is used to handle retrieving all classrooms.
     * @param query The get all classrooms query.
     * @return A list of all classrooms in the system.
     * @see GetAllClassroomsQuery
     * @see Classroom
     */
    @Override
    public List<Classroom> handle(GetAllClassroomsQuery query) {
        return classroomRepository.findAll();
    }

    /**
     * This method is used to handle retrieving a classroom by its ID.
     * @param query The get classroom by ID query containing the classroom ID.
     * @return An optional with the classroom if found, otherwise an empty optional.
     * @see GetClassroomByIdQuery
     * @see Classroom
     */
    @Override
    public Optional<Classroom> handle(GetClassroomByIdQuery query) {
        return classroomRepository.findById(query.classroomId());
    }
}