    import java.util.List;

    public class Clasificador {

        public String clasificar(Double nuevaTemperatura, List<Dato> datos) {
            Double suma = 0.0;

            for (Dato dat : datos) {
                suma += dat.getTemp();
            }

            Double promedio = suma / datos.size();

            if (nuevaTemperatura >= promedio) {
                return "Alto";
            } else {
                return "Bajo";
            }
        }
    }


