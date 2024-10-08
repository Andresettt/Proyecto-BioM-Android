package com.example.aplicacion_3ro;

/**
 * @brief Clase que representa una medición enviada al servidor.
 *
 * Esta clase contiene los atributos de una medición: el valor del dato,
 * el tipo de medición y el identificador del usuario que realiza la medición.
 */
public class Medicion {

    /**
     * @brief Valor de la medición.
     *
     * Representa el valor de la medición que se quiere enviar al servidor.
     */
    private int dato;

    /**
     * @brief Identificador del tipo de medición.
     *
     * Este valor indica el tipo de medición que se está realizando, por ejemplo,
     * si es una medición de temperatura o de ozono.
     */
    private int id_tipo;

    /**
     * @brief Identificador del usuario.
     *
     * El ID del usuario que está realizando la medición.
     */
    private int id_usuario;

    /**
     * @brief Constructor de la clase Medicion.
     *
     * Inicializa una nueva instancia de la clase Medicion con los valores especificados.
     *
     * @param dato El valor de la medición.
     * @param id_tipo El identificador del tipo de medición.
     * @param id_usuario El identificador del usuario que realiza la medición.
     */
    public Medicion(int dato, int id_tipo, int id_usuario) {
        this.dato = dato;
        this.id_tipo = id_tipo;
        this.id_usuario = id_usuario;
    }

    // Getters y setters (si los necesitas)

    /**
     * @brief Obtiene el valor de la medición.
     * @return El valor de la medición.
     */
    public int getDato() {
        return dato;
    }

    /**
     * @brief Establece el valor de la medición.
     * @param dato El valor a establecer.
     */
    public void setDato(int dato) {
        this.dato = dato;
    }

    /**
     * @brief Obtiene el identificador del tipo de medición.
     * @return El identificador del tipo de medición.
     */
    public int getId_tipo() {
        return id_tipo;
    }

    /**
     * @brief Establece el identificador del tipo de medición.
     * @param id_tipo El identificador del tipo de medición a establecer.
     */
    public void setId_tipo(int id_tipo) {
        this.id_tipo = id_tipo;
    }

    /**
     * @brief Obtiene el identificador del usuario.
     * @return El identificador del usuario.
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * @brief Establece el identificador del usuario.
     * @param id_usuario El identificador del usuario a establecer.
     */
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
