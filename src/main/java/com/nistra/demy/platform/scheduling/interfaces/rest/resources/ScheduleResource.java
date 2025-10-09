package com.nistra.demy.platform.scheduling.interfaces.rest.resources;

/**
 * Recurso híbrido para una sesión de horario, con IDs necesarios para comandos
 * y datos enriquecidos de Curso, Profesor y Salón para la visualización.
 */
public record ScheduleResource(
        Long id,
        String startTime,
        String endTime,
        String dayOfWeek,

        // IDs originales (Necesarias para comandos PUT, DELETE)
        Long courseId,
        Long classroomId,
        Long teacherId,

        // Campos descriptivos (Nuevos para la visualización)
        String courseName,
        String courseCode,
        String teacherFirstName,
        String teacherLastName,
        String classroomCode,
        String classroomCampus
) {
}