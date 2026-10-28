package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de interfaz para la gestión, consulta y catálogo de disciplinas deportivas del club.
 * <p>
 * Aplica los principios <b>DIP (Inversión de Dependencias)</b> e <b>ISP (Segregación de Interfaces)</b> de SOLID,
 * garantizando que la capa de presentación (GUI Swing) y los servicios de inscripción dependan exclusivamente de esta abstracción.
 * </p>
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public interface IDisciplineService {

    /**
     * Obtiene el listado completo de disciplinas deportivas activas en el club.
     *
     * @return Lista inmutable de nombres de disciplinas. Nunca retorna {@code null}.
     * @throws PersistenceException Si ocurre un error de lectura en el almacenamiento.
     */
    List<String> getAvailableDisciplines() throws PersistenceException;

    /**
     * Registra una nueva disciplina deportiva en la oferta del club.
     * <p>
     * Cumple con el principio <b>OCP (Abierto/Cerrado)</b> al permitir extender la oferta deportiva
     * (ej. Voleibol, Natación) sin modificar los componentes existentes.
     * </p>
     *
     * @param disciplineName Nombre de la disciplina a registrar.
     * @return {@code true} si la disciplina se registró exitosamente; {@code false} si ya existía.
     * @throws PersistenceException Si ocurre un error al persistir los datos.
     * @throws IllegalArgumentException Si el nombre ingresado es nulo o está en blanco.
     */
    boolean registerDiscipline(String disciplineName) throws PersistenceException;

    /**
     * Verifica si una disciplina deportiva específica se encuentra registrada y activa.
     *
     * @param disciplineName Nombre de la disciplina a consultar.
     * @return {@code true} si la disciplina existe; {@code false} en caso contrario.
     * @throws PersistenceException Si ocurre un error de consulta.
     */
    boolean isDisciplineAvailable(String disciplineName) throws PersistenceException;

    /**
     * Busca la denominación oficial de una disciplina ignorando mayúsculas y minúsculas.
     *
     * @param disciplineName Nombre o fragmento del nombre de la disciplina.
     * @return Un {@link Optional} con el nombre formateado de la disciplina si existe.
     * @throws PersistenceException Si ocurre un error de búsqueda.
     */
    Optional<String> findDisciplineByName(String disciplineName) throws PersistenceException;

    /**
     * Obtiene el total de disciplinas deportivas registradas.
     *
     * @return Cantidad entera de disciplinas activas.
     * @throws PersistenceException Si ocurre un error al contar los registros.
     */
    int getTotalDisciplinesCount() throws PersistenceException;
}