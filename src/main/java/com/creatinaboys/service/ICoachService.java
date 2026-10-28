package com.creatinaboys.service;

import com.creatinaboys.model.CategoryEnum;

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
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public interface ICoachService {

    /**
     * Asigna formalmente a un entrenador a una disciplina y categoría de edad específica.
     *
     * @param coachId Documento de identidad o código único del entrenador.
     * @param discipline Nombre de la disciplina deportiva (ej. "Football", "Basketball").
     * @param category Categoría de edad correspondiente ({@link CategoryEnum}).
     * @return {@code true} si la asignación fue realizada con éxito.
     * @throws IllegalArgumentException Si alguno de los parámetros requeridos es nulo o está vacío.
     */
    boolean assignCoachToCategory(String coachId, String discipline, CategoryEnum category);

    /**
     * Obtiene una lista con todos los entrenadores registrados actualmente en el club.
     *
     * @return Lista inmutable o protegida de entrenadores. Nunca retorna {@code null}.
     */
    List<Object> getAllCoaches();

    /**
     * Busca a un entrenador por su documento de identidad de forma segura usando {@link Optional}.
     *
     * @param coachId Identificación única del entrenador.
     * @return Un {@link Optional} que contiene al entrenador si existe, o un {@code Optional.empty()} en caso contrario.
     */
    Optional<Object> findCoachById(String coachId);

    /**
     * Obtiene los entrenadores asignados a una disciplina específica utilizando filtrado por Streams.
     *
     * @param discipline Nombre de la disciplina deportiva.
     * @return Lista de entrenadores vinculados a la disciplina.
     */
    List<Object> getCoachesByDiscipline(String discipline);

    /**
     * Obtiene los entrenadores asignados a una categoría de edad.
     *
     * @param category Constante del enumerado {@link CategoryEnum}.
     * @return Lista de entrenadores que dirigen la categoría indicada.
     */
    List<Object> getCoachesByCategory(CategoryEnum category);

    /**
     * Agrupa y cuenta el total de entrenadores asignados por cada disciplina deportiva.
     *
     * @return Mapa asociativo con el nombre de la disciplina y su número de entrenadores.
     */
    Map<String, Long> getCoachesCountByDiscipline();

    /**
     * Obtiene el número total de entrenadores activos en la institución.
     *
     * @return Cantidad entera de entrenadores registrados.
     */
    int getTotalCoachesCount();
}