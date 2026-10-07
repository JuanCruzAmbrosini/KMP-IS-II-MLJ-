public class IAClasica {

    public static String recomendarActividad(String clima, Double temp, boolean esFinde) {

        if (clima.equals("soleado")) {
            if (temp > 30) {
                if (esFinde) {
                    return "Ir a la pileta";
                } else {
                    return "Tomar algo fresco";
                }
            } else if (temp > 20) {
                if (esFinde) {
                    return "Salir a pasear en auto";
                } else {
                    return "Salir a caminar cerca";
                }
            } else {
                return "Abrigarse pero salir igual";
            }
        } else if (clima.equals("lluvia")) {
            if (esFinde) {
                return "Ver películas en casa";
            } else {
                return "Quedarse en casa trabajando";
            }

        } else if (clima.equals("nublado")) {
            if (temp > 25) {
                return "Salir igual, no hace tanto calor";
            } else {
                return "Quedarse en casa o salir poco";
            }

        } else {
            return "No se puede determinar la actividad";
        }
    }
}





