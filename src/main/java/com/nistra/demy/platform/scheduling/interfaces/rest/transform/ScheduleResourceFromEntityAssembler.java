package com.nistra.demy.platform.scheduling.interfaces.rest.transform;

import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherByFullNameQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherByIdQuery;
import com.nistra.demy.platform.institution.domain.services.TeacherQueryService;
import com.nistra.demy.platform.scheduling.domain.model.entities.Schedule;
import com.nistra.demy.platform.scheduling.domain.model.queries.GetClassroomByIdQuery;
import com.nistra.demy.platform.scheduling.domain.model.queries.GetCourseByIdQuery;
import com.nistra.demy.platform.scheduling.domain.services.ClassroomQueryService;
import com.nistra.demy.platform.scheduling.domain.services.CourseQueryService;
import com.nistra.demy.platform.scheduling.interfaces.rest.resources.ScheduleResource;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ScheduleResourceFromEntityAssembler {

    // 1. Variables estáticas para almacenar los servicios inyectados
    private static CourseQueryService staticCourseQueryService;
    private static ClassroomQueryService staticClassroomQueryService;
    private static TeacherQueryService staticTeacherQueryService;
    // 2. Variables de instancia para inyección de Spring (constructor)
    private final CourseQueryService courseQueryService;
    private final ClassroomQueryService classroomQueryService;
    private final TeacherQueryService teacherQueryService;

    // Constructor para que Spring inyecte los servicios
    public ScheduleResourceFromEntityAssembler(
            CourseQueryService courseQueryService,
            ClassroomQueryService classroomQueryService,
            TeacherQueryService teacherQueryService) {
        this.courseQueryService = courseQueryService;
        this.classroomQueryService = classroomQueryService;
        this.teacherQueryService = teacherQueryService;
    }

    // 3. Inicializa las variables estáticas después de la inyección
    @PostConstruct
    public void initStaticServices() {
        staticCourseQueryService = this.courseQueryService;
        staticClassroomQueryService = this.classroomQueryService;
        staticTeacherQueryService = this.teacherQueryService;
    }

    /**
     * Convierte una entidad Schedule en un ScheduleResource de FORMA ESTÁTICA,
     * usando los servicios almacenados estáticamente para enriquecer los datos.
     */
    public static ScheduleResource toResourceFromEntity(Schedule entity) {
        // Validación de contexto: asegurar que los servicios estáticos estén inicializados
        if (staticCourseQueryService == null || staticClassroomQueryService == null || staticTeacherQueryService == null) {
            throw new IllegalStateException("The static services have not been initialized by Spring context. Check if the application is running.");
        }

        // IDs (se mantienen en el recurso)
        Long courseId = entity.getCourseId().id();
        Long classroomId = entity.getClassroomId().id();
        Long teacherId = entity.getTeacherId().userId();

        // Obtener detalles de Curso, Salón y Profesor usando los servicios estáticos
        var course = staticCourseQueryService.handle(new GetCourseByIdQuery(courseId))
                .orElseThrow(() -> new IllegalStateException("Course not found for ID: " + courseId));

        var classroom = staticClassroomQueryService.handle(new GetClassroomByIdQuery(classroomId))
                .orElseThrow(() -> new IllegalStateException("Classroom not found for ID: " + classroomId));

        var teacher = staticTeacherQueryService.handle(new GetTeacherByIdQuery(teacherId))
                .orElseThrow(() -> new IllegalStateException("Teacher UserAccount not found for ID: " + teacherId));

        return new ScheduleResource(
                entity.getId(),
                entity.getTimeRange().startTime().toString(),
                entity.getTimeRange().endTime().toString(),
                entity.getDayOfWeek().name(),

                // IDs Originales
                courseId,
                classroomId,
                teacherId,

                // Campos Descriptivos
                course.getName(),
                course.getCode(),
                teacher.getPersonName().firstName(),
                teacher.getPersonName().lastName(),
                classroom.getCode(),
                classroom.getCampus()
        );
    }
}