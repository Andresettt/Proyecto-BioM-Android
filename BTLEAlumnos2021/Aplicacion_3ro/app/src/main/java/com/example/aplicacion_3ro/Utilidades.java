package com.example.aplicacion_3ro;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @brief Clase de utilidades para conversiones de datos y manipulación de UUIDs.
 *
 * Esta clase incluye métodos para convertir cadenas a bytes, UUIDs a cadenas, conversión
 * de bytes a tipos primitivos como int y long, y otros tipos de conversiones útiles.
 */
public class Utilidades {

    /**
     * @brief Convierte un String a un arreglo de bytes.
     * @param texto El texto a convertir.
     * @return Un arreglo de bytes que representa el texto.
     */
    public static byte[] stringToBytes(String texto) {
        return texto.getBytes();
    }

    /**
     * @brief Convierte un string UUID a un objeto UUID.
     *
     * El string debe tener una longitud de 16 caracteres.
     *
     * @param uuid String con la representación del UUID.
     * @return El objeto UUID correspondiente.
     * @throws Error Si el string no tiene 16 caracteres.
     */
    public static UUID stringToUUID(String uuid) {
        if (uuid.length() != 16) {
            throw new Error("stringUUID: string no tiene 16 caracteres");
        }
        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        return new UUID(Utilidades.bytesToLong(masSignificativo.getBytes()), Utilidades.bytesToLong(menosSignificativo.getBytes()));
    }

    /**
     * @brief Convierte un objeto UUID a String.
     * @param uuid El objeto UUID a convertir.
     * @return Una representación en String del UUID.
     */
    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    /**
     * @brief Convierte un UUID en su representación hexadecimal.
     * @param uuid El objeto UUID a convertir.
     * @return Una representación en hexadecimal del UUID como String.
     */
    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    /**
     * @brief Convierte un arreglo de bytes en String.
     * @param bytes Arreglo de bytes.
     * @return String equivalente al arreglo de bytes.
     */
    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    /**
     * @brief Convierte dos valores long en un arreglo de bytes.
     *
     * Este método combina dos valores long (más y menos significativos) en un arreglo de bytes.
     *
     * @param masSignificativos Bits más significativos del UUID.
     * @param menosSignificativos Bits menos significativos del UUID.
     * @return Un arreglo de bytes que combina los dos long.
     */
    public static byte[] dosLongToBytes(long masSignificativos, long menosSignificativos) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
        buffer.putLong(masSignificativos);
        buffer.putLong(menosSignificativos);
        return buffer.array();
    }

    /**
     * @brief Convierte un arreglo de bytes a un entero.
     * @param bytes Arreglo de bytes.
     * @return Entero equivalente al arreglo de bytes.
     */
    public static int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    /**
     * @brief Convierte un arreglo de bytes a un long.
     * @param bytes Arreglo de bytes.
     * @return Long equivalente al arreglo de bytes.
     */
    public static long bytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    /**
     * @brief Convierte un arreglo de bytes a entero (int) manejando los casos de signo.
     *
     * Este método convierte bytes a entero utilizando el concepto de "complemento a 2" para manejar signos negativos.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return El valor entero resultante.
     * @throws Error Si el arreglo de bytes es mayor a 4 bytes (tamaño de int).
     */
    public static int bytesToIntOK(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        if (bytes.length > 4) {
            throw new Error("demasiados bytes para pasar a int");
        }

        int res = 0;
        for (byte b : bytes) {
            res = (res << 8) + (b & 0xFF); // Desplazamiento y conversión a 1 byte.
        }

        // Verifica si es un número negativo en complemento a 2.
        if ((bytes[0] & 0x8) != 0) {
            res = -(~(byte) res) - 1;
        }

        return res;
    }

    /**
     * @brief Convierte un arreglo de bytes a un String en formato hexadecimal.
     *
     * Cada byte se convierte a dos caracteres hexadecimales.
     *
     * @param bytes Arreglo de bytes.
     * @return String en formato hexadecimal con los bytes separados por ":".
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    }
} // class
