package com.creatinaboys.service;

import com.creatinaboys.exceptions.CoachNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrato de interfaz para la gestión operativa, asignación y consulta de entrenadores del club deportivo.
 * <p>
 * Aplica los principios <b>DIP (Inversión de Dependencias)</b> e <b>ISP (Segregación de Interfaces)</b> de SOLID,
 * permitiendo que las vistas gráficas y los controladores de la aplicación interactúen exclusivamente con esta abstracción.
 * </p>
 * 
 * @author Milton Andrés Maldonado Salcedo & Jesus Martinez Baute
 * @version 1.1
 */
public interface ICoachService {

    /**
     * Registra un nuevo entrenador en el sistema.
     *
     * @param coach Entidad del entrenador a registrar.
     * @return {@code true} si el registro fue exitoso; {@code false} si ya existe un entrenador con ese documento.
     * @throws PersistenceException Si ocurre un error durante la persistencia en el archivo.
     */
    boolean registerCoach(Coach coach) throws PersistenceException;

    /**
     * Asigna formalmente a un entrenador a una disciplina y categoría de edad específica.
     *
     * @param coachId Documento de identidad o código único del entrenador.
     * @param discipline Nombre de la disciplina deportiva (ej. "Football", "Basketball").
     * @param category Categoría de edad correspondiente ({@link CategoryEnum}).
     * @return {@code true} si la asignación fue realizada con éxito.
     * @throws CoachNotFoundException Si el entrenador no se encuentra en el sistema.
     * @throws PersistenceException Si ocurre un error al actualizar los datos.
     */
    boolean assignCoachToCategory(String coachId, String discipline, CategoryEnum category) 
            throws CoachNotFoundException, PersistenceException;

    /**
     * Obtiene una lista con todos los entrenadores registrados actualmente en el club.
     *
     * @return Lista de entrenadores. Nunca retorna {@code null}.
     * @throws PersistenceException Si ocurre un error al leer la información.
     */
    List<Coach> getAllCoaches() throws PersistenceException;

    /**
     * Busca a un entrenador por su documento de identidad de forma segura usando {@link Optional}.
     *
     * @param coachId Identificación única del entrenador.
     * @return Un {@link Optional} que contiene al entrenador si existe, o {@code Optional.empty()} en caso contrario.
     * @throws PersistenceException Si ocurre un error durante la búsqueda.
     */
    Optional<Coach> findCoachById(String coachId) throws PersistenceException;

    /**
     * Obtiene los entrenadores asignados a una disciplina específica utilizando filtrado por Streams.
     *
     * @param discipline Nombre de la disciplina deportiva.
     * @return Lista de entrenadores vinculados a la disciplina.
     * @throws PersistenceException Si ocurre un error al leer la información.
     */
    List<Coach> getCoachesByDiscipline(String discipline) throws PersistenceException;

    /**
     * Obtiene los entrenadores asignados a una categoría de edad.
     *
     * @param category Constante del enumerado {@link CategoryEnum}.
     * @return Lista de entrenadores que dirigen la categoría indicada.
     * @throws PersistenceException Si ocurre un error al leer la información.
     */
    List<Coach> getCoachesByCategory(CategoryEnum category) throws PersistenceException;

    /**
     * Actualiza la información de un entrenador existente.
     *
     * @param coach Entidad con los datos actualizados.
     * @return {@code true} si se actualizó correctamente.
     * @throws CoachNotFoundException Si el entrenador a actualizar no existe.
     * @throws PersistenceException Si ocurre un error durante la actualización.
     */
    boolean updateCoach(Coach coach) throws CoachNotFoundException, PersistenceException;

    /**
     * Elimina a un entrenador del sistema por su identificación.
     *
     * @param coachId Documento del entrenador a eliminar.
     * @return {@code true} si fue eliminado exitosamente.
     * @throws CoachNotFoundException Si el entrenador no existe.
     * @throws PersistenceException Si ocurre un error al reescribir el archivo.
     */
    boolean deleteCoach(String coachId) throws CoachNotFoundException, PersistenceException;

    /**
     * Agrupa y cuenta el total de entrenadores asignados por cada disciplina deportiva.
     *
     * @return Mapa asociativo con el nombre de la disciplina y su número de entrenadores.
     * @throws PersistenceException Si ocurre un error en la lectura de datos.
     */
    Map<String, Long> getCoachesCountByDiscipline() throws PersistenceException;

    /**
     * Obtiene el número total de entrenadores activos en la institución.
     *
     * @return Cantidad entera de entrenadores registrados.
     * @throws PersistenceException Si ocurre un error en la lectura de datos.
     */
    int getTotalCoachesCount() throws PersistenceException;
}