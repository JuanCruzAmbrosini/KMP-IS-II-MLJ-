export const traducirDescripcion = (descripcion) => {
    if (!descripcion) return ''

    const descNormalizada = String(descripcion).toLowerCase().trim()

    // Mapeo de traducciones de inglés a español
    const traducciones = {
        "clear sky": "cielo despejado",
        "few clouds": "pocas nubes",
        "scattered clouds": "nubes dispersas",
        "broken clouds": "nubes rotas",
        "overcast clouds": "nublado",
        "shower rain": "chubascos",
        "light rain": "lluvia ligera",
        "moderate rain": "lluvia moderada",
        "heavy intensity rain": "lluvia intensa",
        "very heavy rain": "lluvia muy intensa",
        "extreme rain": "lluvia torrencial",
        "freezing rain": "lluvia helada",
        "light intensity shower rain": "chubascos ligeros",
        "heavy intensity shower rain": "chubascos intensos",
        "ragged shower rain": "chubascos irregulares",
        "drizzle": "llovizna",
        "light intensity drizzle": "llovizna ligera",
        "heavy intensity drizzle": "llovizna intensa",
        "rain": "lluvia",
        "thunderstorm": "tormenta eléctrica",
        "thunderstorm with light rain": "tormenta con lluvia ligera",
        "thunderstorm with rain": "tormenta con lluvia",
        "thunderstorm with heavy rain": "tormenta con lluvia fuerte",
        "snow": "nieve",
        "light snow": "nieve ligera",
        "heavy snow": "nieve intensa",
        "sleet": "aguanieve",
        "mist": "niebla",
        "smoke": "humo",
        "haze": "neblina",
        "dust": "polvo",
        "fog": "niebla",
        "sand": "arena",
        "ash": "ceniza",
        "squall": "chubasco",
        "tornado": "tornado"
    };

    return traducciones[descNormalizada] || descripcion;
}