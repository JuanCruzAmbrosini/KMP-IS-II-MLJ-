//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    List<Dato> datos = new ArrayList<>();
    datos.add(new Dato(10.0, "Bajo"));
    datos.add(new Dato(20.0, "Bajo"));
    datos.add(new Dato(30.0, "Alto"));
    datos.add(new Dato(80.0, "Alto"));
    datos.add(new Dato(90.0, "Alto"));

    Clasificador clasif = new Clasificador();

    Double nuevaTemperatura = 30.0;

    System.out.println("Resultado es: " +
            clasif.clasificar(nuevaTemperatura, datos));

}