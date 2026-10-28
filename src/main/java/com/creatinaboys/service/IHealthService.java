package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.FitnessStatusEnum;
import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.model.InjuryRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de interfaz para la gestión médica, fichas de salud y seguimiento de aptitud física de los deportistas.
 * <p>
 * Aplica los principios <b>DIP (Inversión de Dependencias)</b> e <b>ISP (Segregación de Interfaces)</b> de SOLID,
 * garantizando que la capa de presentación (GUI Swing) y otros servicios dependan exclusivamente de esta abstracción.
 * </p>
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public interface IHealthService {

    /**
     * Guarda o actualiza la ficha médica de un atleta validando su existencia previa en el sistema.
     *
     * @param record Instancia de {@link HealthRecord} que contiene los datos médicos del deportista.
     * @return {@code true} si la ficha médica fue procesada y persistida con éxito.
     * @throws AthleteNotFoundException Si el atleta vinculado a la ficha no existe en el repositorio.
     * @throws PersistenceException Si ocurre un error en la capa de persistencia.
     * @throws IllegalArgumentException Si el objeto {@code record} es nulo.
     */
    boolean saveOrUpdateHealthRecord(HealthRecord record) throws AthleteNotFoundException, PersistenceException;

    /**
     * Obtiene la ficha médica de un deportista a partir de su documento de identidad.
     *
     * @param athleteId Documento de identidad único del atleta.
     * @return Un {@link Optional} con la ficha {@link HealthRecord} si existe, o {@code Optional.empty()} en caso contrario.
     * @throws PersistenceException Si ocurre un error de lectura en el almacenamiento.
     * @throws IllegalArgumentException Si {@code athleteId} es nulo o está en blanco.
     */
    Optional<HealthRecord> getHealthRecordByAthleteId(String athleteId) throws PersistenceException;

    /**
     * Registra una nueva lesión en la ficha médica de un deportista.
     *
     * @param athleteId Documento de identidad del atleta.
     * @param injury Instancia de {@link InjuryRecord} con los detalles de la lesión.
     * @return {@code true} si la lesión se agregó y guardó correctamente.
     * @throws AthleteNotFoundException Si el atleta no existe en el repositorio.
     * @throws PersistenceException Si ocurre un error al persistir el registro.
     * @throws IllegalArgumentException Si el objeto {@code injury} es nulo.
     */
    boolean addInjuryToAthlete(String athleteId, InjuryRecord injury) throws AthleteNotFoundException, PersistenceException;

    /**
     * Actualiza el estado de aptitud médica, observaciones y la fecha de valoración de un deportista.
     *
     * @param athleteId Documento de identidad del atleta.
     * @param newStatus Nuevo estado de aptitud médica ({@link FitnessStatusEnum}).
     * @param restrictions Descripción de observaciones, cuidados o restricciones médicas.
     * @param assessmentDate Fecha en la que se realizó la evaluación médica.
     * @return {@code true} si la actualización del estado médico fue exitosa.
     * @throws AthleteNotFoundException Si el atleta no se encuentra en el repositorio.
     * @throws PersistenceException Si ocurre un error de almacenamiento.
     */
    boolean updateFitnessStatus(String athleteId, FitnessStatusEnum newStatus, String restrictions, LocalDate assessmentDate)
            throws AthleteNotFoundException, PersistenceException;

    /**
     * Valida si un atleta está habilitado físicamente para realizar actividades de entrenamiento.
     *
     * @param athleteId Documento de identidad del atleta.
     * @return {@code true} si el atleta está apto para entrenar; {@code false} si tiene restricción médica.
     * @throws PersistenceException Si ocurre un error al consultar el registro médico.
     */
    boolean isAthleteFitToTrain(String athleteId) throws PersistenceException;

    /**
     * Retorna todas las fichas de salud registradas en el club.
     *
     * @return Lista completa de objetos {@link HealthRecord}.
     * @throws PersistenceException Si ocurre un error al consultar los datos.
     */
    List<HealthRecord> getAllHealthRecords() throws PersistenceException;
}