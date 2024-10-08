package com.example.aplicacion_3ro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @brief Interfaz para definir las llamadas HTTP de la API.
 *
 * Esta interfaz define el método para enviar las mediciones al servidor
 * utilizando una solicitud HTTP POST. Retrofit se encargará de crear la implementación
 * de esta interfaz en tiempo de ejecución.
 */
public interface ApiService {

    /**
     * @brief Envía una medición al servidor.
     *
     * Este método realiza una solicitud POST al endpoint "/mediciones" para enviar
     * los datos de una medición al servidor.
     *
     * @param medicion El objeto de tipo Medicion que contiene los datos a enviar.
     * @return Call<Void> Objeto Call de Retrofit que representa la solicitud HTTP.
     */
    @POST("/mediciones")
    Call<Void> enviarMediciones(@Body Medicion medicion);
}
