//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {

    String clima = "parcialmente soleado";
    Double temperatura = 42.0;
    boolean esFinde = true;

    System.out.println("El plan que te recomiendo es: " +
            IAClasica.recomendarActividad(clima,
            temperatura, esFinde));

}
