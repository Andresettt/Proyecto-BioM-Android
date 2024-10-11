package com.example.aplicacion_3ro;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aplicacion_3ro.R;
import com.example.aplicacion_3ro.TramaIBeacon;
import com.example.aplicacion_3ro.Utilidades;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * @class MainActivity
 * @brief Clase principal de la aplicación que maneja la interfaz de usuario y la interacción con Bluetooth LE.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Etiqueta de log para el seguimiento de eventos.
     */
    private static final String ETIQUETA_LOG = ">>>>";

    /**
     * Código de petición para los permisos requeridos.
     */
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    /**
     * Escáner Bluetooth Low Energy (BLE) utilizado para buscar dispositivos.
     */
    private BluetoothLeScanner elEscanner;

    /**
     * Callback utilizado para manejar los resultados del escaneo BLE.
     */
    private ScanCallback callbackDelEscaneo = null;

    /**
     * TextView para mostrar el valor de la temperatura.
     */
    public TextView valor1;

    /**
     * TextView para mostrar el valor del ozono.
     */
    public TextView valor2;

    /**
     * Servicio API para enviar datos al servidor.
     */
    private ApiService apiService;

    /**
     * @param savedInstanceState Estado previamente guardado de la actividad.
     * @brief Método llamado al crear la actividad. Inicializa la vista y configura el escáner Bluetooth.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valor1 = findViewById(R.id.ValorTemp);
        valor2 = findViewById(R.id.ValorOzono);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            elEscanner = bluetoothAdapter.getBluetoothLeScanner();
        } else {
            // Manejar el caso en que Bluetooth no está disponible o habilitado
        }

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.132.145:13000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * @param idTipo    Tipo de medición.
     * @param valor     Valor de la medición.
     * @param idUsuario ID del usuario.
     * @brief Envía los datos al servidor utilizando Retrofit.
     */
    private void enviarDatosAlServidor(final int idTipo, final int valor, final int idUsuario) {
        Medicion medicion = new Medicion(valor, idTipo, idUsuario);

        // Llamada a Retrofit
        Call<Void> call = apiService.enviarMediciones(medicion);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("SERVIDOR", "Datos enviados con éxito.");
                } else {
                    Log.d("SERVIDOR", "Error al enviar datos. Código de respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("SERVIDOR", "Error al enviar datos al servidor: " + t.getMessage());
            }
        });
    }

    /**
     * @brief Inicia la búsqueda de todos los dispositivos Bluetooth LE.
     */
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): instalamos scan callback");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanResult()");
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanFailed()");
            }
        };

        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empezamos a escanear");

        this.elEscanner.startScan(this.callbackDelEscaneo);
    }


    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @param resultado Resultado del escaneo del dispositivo Bluetooth LE.
     * @brief Muestra la información detallada del dispositivo Bluetooth LE detectado.
     */
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();

        // Verificar si hay datos en ScanRecord
        if (resultado.getScanRecord() == null) {
            Log.d(ETIQUETA_LOG, "No hay datos de ScanRecord disponibles.");
            return;
        }

        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        // Log de información del dispositivo detectado
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);
        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        // Analizar los datos como una trama iBeacon
        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, " advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, " advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, " companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, " iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, " iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( " + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( " + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( " + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");
    }

    /**
     * @param majorValue Valor 'major' extraído del iBeacon.
     * @return 2 si el sensor es de ozono, 1 si es de temperatura, -1 si es desconocido.
     * @brief Determina el tipo de sensor basándose en el valor de 'major'.
     */
    private int obtenerTipoDeSensor(int majorValue) {
        // Extraer el valor de MedicionesID desplazando 8 bits (por el contador)
        int medicionID = (majorValue >> 8); // Desplaza 8 bits a la derecha para obtener el MedicionesID

        if (medicionID == 11) {
            return 2; // Sensor de Ozono
        } else if (medicionID == 12) {
            return 1; // Sensor de Temperatura
        } else {
            return -1; // Tipo de sensor desconocido
        }
    }


    /**
     * @param dispositivoBuscado UUID del dispositivo Bluetooth LE que se está buscando.
     * @brief Inicia un escaneo Bluetooth LE para buscar un dispositivo con un UUID específico.
     * <p>
     * Esta función busca un dispositivo Bluetooth LE cuyo UUID coincida con el proporcionado.
     * Si se encuentra el dispositivo, se extrae información adicional como el valor major y minor,
     * se determina el tipo de sensor y se actualizan los valores de temperatura u ozono en la interfaz.
     */
    private void buscarEsteDispositivoBTLE(final UUID dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empieza");

        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): instalamos scan callback");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanResult()");

                // Obtén el dispositivo desde el resultado del escaneo
                BluetoothDevice dispositivo = resultado.getDevice();
                if (resultado.getScanRecord() == null) {
                    Log.d(ETIQUETA_LOG, "No hay datos de ScanRecord disponibles.");
                    return;
                }
                byte[] bytes = resultado.getScanRecord().getBytes();

                // Crear objeto TramaIBeacon para extraer el UUID
                TramaIBeacon tib = new TramaIBeacon(bytes);
                UUID uuid = Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID()));

                // Comprueba si el UUID coincide con el dispositivo buscado
                if (uuid.equals(dispositivoBuscado)) {
                    Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + uuid);
                    mostrarInformacionDispositivoBTLE(resultado);

                    // Extraer los valores major y minor
                    byte[] majorBytes = tib.getMajor();
                    int majorValue = Utilidades.bytesToInt(majorBytes);

                    byte[] minorBytes = tib.getMinor();
                    int minorValue = Utilidades.bytesToInt(minorBytes);

                    // Determinar el tipo de sensor (Ozono o Temperatura)
                    int tipoSensor = obtenerTipoDeSensor(majorValue);

                    // Enviar los datos al servidor
                    enviarDatosAlServidor(tipoSensor, minorValue, 1);

                    // Actualizar la interfaz con el valor del sensor detectado
                    if (tipoSensor == 1) {
                        Log.d(ETIQUETA_LOG, "Medición de temperatura detectada");
                        final String temperaturaText = minorValue + " °C";
                        valor1.setText(temperaturaText); // Actualiza el valor de temperatura
                    } else if (tipoSensor == 2) {
                        Log.d(ETIQUETA_LOG, "Medición de Ozono detectada");
                        final String OzonoText = minorValue + " ppm";
                        valor2.setText(OzonoText); // Actualiza el valor de Ozono
                    } else {
                        Log.d(ETIQUETA_LOG, "Tipo de medición desconocida. Major: " + majorValue);
                    }
                } else {
                    Log.d(ETIQUETA_LOG, "Dispositivo no objetivo encontrado: " + uuid);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanFailed()");
            }
        };

        // Configuración de escaneo (rápido)
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)  // Modo de escaneo rápido
                .build();

        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);

        // Iniciar el escaneo sin filtros
        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
    }


    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @param v Vista que representa el botón pulsado.
     * @brief Método invocado al pulsar el botón para buscar un dispositivo BTLE específico.
     * <p>
     * Este método utiliza un UUID predefinido y llama a la función para iniciar la búsqueda
     * de dicho dispositivo mediante Bluetooth LE.
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton nuestro dispositivo BTLE Pulsado");
        // Utiliza un UUID válido en lugar de una cadena arbitraria
        UUID uuidBuscado = Utilidades.stringToUUID("EQUIPO-ANDRES-3A"); // Reemplaza con tu UUID
        this.buscarEsteDispositivoBTLE(uuidBuscado);
    } // botonBuscarNuestroDispositivoBTLEPulsado()

// --------------------------------------------------------------

    /**
     * @brief Detiene la búsqueda de dispositivos BTLE en curso.
     * <p>
     * Si la búsqueda está activa, detiene el escaneo y desactiva el callback asociado.
     */
    private void detenerBusquedaDispositivosBTLE() {
        if (this.callbackDelEscaneo == null) {
            return;
        }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;
    } // detenerBusquedaDispositivosBTLE()

// --------------------------------------------------------------

    /**
     * @param v Vista que representa el botón pulsado.
     * @brief Método invocado al pulsar el botón para buscar todos los dispositivos BTLE.
     * <p>
     * Este método inicia una búsqueda para todos los dispositivos Bluetooth LE cercanos
     * llamando a la función correspondiente.
     */
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // botonBuscarDispositivosBTLEPulsado()

// --------------------------------------------------------------

    /**
     * @param v Vista que representa el botón pulsado.
     * @brief Método invocado al pulsar el botón para detener la búsqueda de dispositivos BTLE.
     * <p>
     * Detiene la búsqueda de dispositivos Bluetooth LE en curso.
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // botonDetenerBusquedaDispositivosBTLEPulsado()


    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * @brief Inicializa el adaptador Bluetooth y solicita permisos necesarios.
     * <p>
     * Este método obtiene el adaptador Bluetooth del dispositivo, habilita el Bluetooth si no
     * está habilitado, y solicita los permisos necesarios dependiendo de la versión de Android
     * para realizar escaneos de dispositivos Bluetooth LE.
     * <p>
     * En versiones de Android 12 y superiores, se solicitan permisos para escanear y conectar
     * dispositivos Bluetooth cercanos. En versiones anteriores, se solicitan permisos para
     * Bluetooth y localización.
     */
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: El dispositivo no soporta Bluetooth !!!!");
            return;
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT");

        if (!bta.isEnabled()) {
            boolean habilitado = bta.enable();
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado = " + habilitado);
        } else {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Bluetooth ya está habilitado.");
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): estado = " + bta.getState());
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escaner btle");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle !!!!");
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): voy a pedir permisos (si no los tuviera) !!!!");

        // Comprobamos la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Para Android 12 y versiones posteriores, pedimos permisos de "dispositivos cercanos"
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios en Android 12+ !!!!");
            }
        } else {
            // Para Android 11 y versiones anteriores, pedimos los permisos de Bluetooth y localización antiguos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");
            }
        }
    } // inicializarBlueTooth()

// --------------------------------------------------------------

    /**
     * @param requestCode  Código que identifica la solicitud de permisos.
     * @param permissions  Arreglo con los nombres de los permisos solicitados.
     * @param grantResults Arreglo con los resultados de los permisos (concedidos o denegados).
     * @brief Método que gestiona los resultados de la solicitud de permisos.
     * <p>
     * Este método es llamado cuando el usuario concede o deniega los permisos solicitados.
     * Si los permisos son concedidos, permite continuar con la inicialización o el escaneo.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): permisos concedidos !!!!");
                    // Permission is granted. Puedes iniciar el escaneo o continuar con la inicialización.
                } else {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): Socorro: permisos NO concedidos !!!!");
                }
                return;
        }
    } // onRequestPermissionsResult()
}   //MainActivity
