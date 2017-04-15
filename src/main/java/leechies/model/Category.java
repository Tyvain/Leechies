package leechies.model;

public enum Category {
    // VEHICULE
    VEHICULE_4x4("4x4"),
    VOITURES("Voitures"),
    MOTOS("Motos & scooters"),
    PIECES_DETACHEES("Pièces détachées"),
    VEHICULE_DIVERS("Divers (VEHICULES)"),
    
    // NAUTISME
    VOILIERS("Voiliers"),
    BATEAUX_MOTEUR("Bateaux moteur"),
    NAUTISME_SPORT_LOISIRS("Sport & loisirs"),
    PECHE("Pêche"),
    NAUTISME_DIVERS("Divers (NAUTISME)"),

    // MULTIMEDIA
    INFORMATIQUE("Informatique"),
    CONSOLE_JEUX("Consoles & Jeux vidéo"),
    IMAGE_SON("Image & Son"),
    TELEPHONIE("Téléphonie"),
    MULTIMEDIA_DIVERS("Divers (MULTIMEDIA)"),

    // LOISIRS
    DVD_LIVRES("DVD / CD / Livres"),
    SPORT_HOBBIES("Sports & Hobbies"),
    MUSIQUE("Musique"),
    COLLECTION("Collection"),
    JEUX_JOUETS("Jeux & Jouets"),
    VIN_GASTRONOMIE("Vins & Gastronomie"),
    LOISIRS_DIVERS("Divers (LOISIRS)"),

    // MAISON
    AMEUBLEMENT("Ameublement"),
    ELECTROMENAGER("Electroménager"),
    BRICOLAGE("Bricolage"),
    JARDINAGE("Jardinage"),
    ANIMAUX("Animaux"),
    MAISON_DIVERS("Divers (MAISON)"),
    
    // TROC
    TROC("Troc"),
    
    //PERDU TROUVE
    PERDU_TROUVE("Perdu trouvé"),
    
    // MODE
    FEMMES("Femmes"),
    HOMMES("Hommes"),
    BEBES("Bébés"),
    ACCESSOIRES_BIJOUX("Accessoires & bijoux"),
    MODE_DIVERS("Divers (MODE)"),
    
    // EMPLOI
    OFFRES("Offres"),
    DEMANDES("Demandes"),
    EMPLOI_DIVERS("Divers (EMPLOI)"),
    
    // IMMOBILIER
    VENTES("Ventes"),
    LOCATIONS("Locations"),
    IMMOBILIER_DIVERS("Divers (IMMOBILIER)");
    
    

    public String libelle;

    Category(String l) {
        libelle = l;
    }
}
