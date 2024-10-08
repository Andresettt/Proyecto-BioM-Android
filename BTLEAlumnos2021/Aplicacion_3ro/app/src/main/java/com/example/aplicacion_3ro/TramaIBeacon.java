package com.example.aplicacion_3ro;

import java.util.Arrays;

/**
 * @brief Clase que representa y procesa una trama iBeacon.
 *
 * Esta clase se encarga de interpretar los datos de una trama iBeacon, extrayendo
 * información como el UUID, el major, el minor, y la potencia de transmisión (TxPower),
 * entre otros campos.
 */
public class TramaIBeacon {
    private byte[] prefijo = null; ///< Prefijo de 9 bytes (o 6 bytes si no incluye advFlags).
    private byte[] uuid = null;    ///< UUID de 16 bytes del iBeacon.
    private byte[] major = null;   ///< Campo major de 2 bytes.
    private byte[] minor = null;   ///< Campo minor de 2 bytes.
    private byte txPower = 0;      ///< Valor de la potencia de transmisión (TxPower) de 1 byte.

    private byte[] losBytes;       ///< Arreglo de bytes que representa la trama completa recibida.

    private byte[] advFlags = null; ///< Bandera de publicidad de 3 bytes (solo si está presente).
    private byte[] advHeader = null; ///< Cabecera de la publicidad (advertisement header) de 2 bytes.
    private byte[] companyID = new byte[2]; ///< Identificador de la compañía de 2 bytes.
    private byte iBeaconType = 0; ///< Tipo de iBeacon de 1 byte.
    private byte iBeaconLength = 0; ///< Longitud del iBeacon de 1 byte.

    private boolean noadvFlags; ///< Indica si los advFlags están presentes en la trama.

    /**
     * @brief Constructor que recibe un arreglo de bytes y procesa la trama iBeacon.
     *
     * @param bytes Arreglo de bytes que representa la trama recibida del iBeacon.
     */
    public TramaIBeacon(byte[] bytes ) {
        this.losBytes = bytes;

        // Detecta si la trama contiene o no los advFlags (3 bytes iniciales 0x02, 0x01, 0x06).
        if( losBytes[0] == 02 && losBytes[1] == 01 && losBytes[2] == 06){
            noadvFlags = false;
        }else{
            noadvFlags = true;
        }

        // Si no hay advFlags, los índices se ajustan.
        if(noadvFlags){
            prefijo = Arrays.copyOfRange(losBytes, 0, 5+1); // 6 bytes
            uuid = Arrays.copyOfRange(losBytes, 6, 21+1); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 22, 23+1); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 24, 25+1); // 2 bytes
            txPower = losBytes[26]; // 1 byte

            advHeader = Arrays.copyOfRange(prefijo, 0, 1+1); // 2 bytes
            companyID = Arrays.copyOfRange(prefijo, 2, 3+1); // 2 bytes
            iBeaconType = prefijo[4]; // 1 byte
            iBeaconLength = prefijo[5]; // 1 byte
        } else {
            prefijo = Arrays.copyOfRange(losBytes, 0, 8+1); // 9 bytes
            uuid = Arrays.copyOfRange(losBytes, 9, 24+1); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 25, 26+1); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 27, 28+1); // 2 bytes
            txPower = losBytes[29]; // 1 byte

            advFlags = Arrays.copyOfRange(prefijo, 0, 2+1); // 3 bytes
            advHeader = Arrays.copyOfRange(prefijo, 3, 4+1); // 2 bytes
            companyID = Arrays.copyOfRange(prefijo, 5, 6+1); // 2 bytes
            iBeaconType = prefijo[7]; // 1 byte
            iBeaconLength = prefijo[8]; // 1 byte
        }
    }

    // Getters para acceder a los campos de la trama iBeacon.

    /**
     * @brief Obtiene el prefijo de la trama.
     * @return El prefijo en un arreglo de bytes.
     */
    public byte[] getPrefijo() {
        return prefijo;
    }

    /**
     * @brief Obtiene el UUID de la trama.
     * @return El UUID en un arreglo de bytes.
     */
    public byte[] getUUID() {
        return uuid;
    }

    /**
     * @brief Obtiene el valor del campo major.
     * @return El valor major en un arreglo de bytes.
     */
    public byte[] getMajor() {
        return major;
    }

    /**
     * @brief Obtiene el valor del campo minor.
     * @return El valor minor en un arreglo de bytes.
     */
    public byte[] getMinor() {
        return minor;
    }

    /**
     * @brief Obtiene el valor de la potencia de transmisión (TxPower).
     * @return El valor de TxPower como un byte.
     */
    public byte getTxPower() {
        return txPower;
    }

    /**
     * @brief Obtiene el arreglo de bytes de la trama completa.
     * @return El arreglo de bytes de la trama completa.
     */
    public byte[] getLosBytes() {
        return losBytes;
    }

    /**
     * @brief Obtiene el valor de las banderas de publicidad (advFlags).
     * @return El valor de advFlags en un arreglo de bytes.
     */
    public byte[] getAdvFlags() {
        return advFlags;
    }

    /**
     * @brief Obtiene el encabezado de publicidad (advHeader).
     * @return El encabezado de publicidad en un arreglo de bytes.
     */
    public byte[] getAdvHeader() {
        return advHeader;
    }

    /**
     * @brief Obtiene el identificador de la compañía.
     * @return El identificador de la compañía en un arreglo de bytes.
     */
    public byte[] getCompanyID() {
        return companyID;
    }

    /**
     * @brief Obtiene el tipo de iBeacon.
     * @return El tipo de iBeacon como un byte.
     */
    public byte getiBeaconType() {
        return iBeaconType;
    }

    /**
     * @brief Obtiene la longitud de iBeacon.
     * @return La longitud de iBeacon como un byte.
     */
    public byte getiBeaconLength() {
        return iBeaconLength;
    }
} // class TramaIBeacon
