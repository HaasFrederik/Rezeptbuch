package memory;

import java.nio.file.Path;

public final class PathLib {

	private static final String BASE_FOLDER_NAME = "Rezeptbuch";
	private static final String RECIPE_FOLDER_NAME = "Rezepte";
	private static final String FILTER_FOLDER_NAME = "Filter";
	private static final String INGREDIENT_FOLDER_NAME = "Zutaten";
	private static final String RESTRICTION_FOLDER_NAME = "Restriktionen";
	private static final String COMPONENT_FOLDER_NAME = "Komponenten";

//	public static final String INGREDIENT_FILTER_SIZE_FILE = "ZutatenVorkommen.txt";
//	public static final String RESTRICTION_FILTER_SIZE_FILE = "RestriktionenVorkommen.txt";
//	public static final String COMPONENT_FILTER_SIZE_FILE = "KomponentenVorkommen.txt";
	public static final String FILTER_SIZE_FILE = "Verteilungsliste.txt";

	public static final Path BASE_FOLDER_PATH = Path.of(BASE_FOLDER_NAME);
	public static final Path RECIPE_FOLDER_PATH = BASE_FOLDER_PATH.resolve(RECIPE_FOLDER_NAME);
	public static final Path FILTER_FOLDER_PATH = BASE_FOLDER_PATH.resolve(FILTER_FOLDER_NAME);
	public static final Path INGREDIENT_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(INGREDIENT_FOLDER_NAME);
	public static final Path RESTRICTION_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(RESTRICTION_FOLDER_NAME);
	public static final Path COMPONENT_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(COMPONENT_FOLDER_NAME);

	public static final Path ALL_RECIPES_FILE_PATH = RECIPE_FOLDER_PATH.resolve("alle.txt");

//	public static final Path[] ALL_FILTER_OCCURENCE_FILE_PATHS = { RESTRICTION_FOLDER_PATH.resolve(FILTER_SIZE_FILE),
//			COMPONENT_FOLDER_PATH.resolve(FILTER_SIZE_FILE), INGREDIENT_FOLDER_PATH.resolve(FILTER_SIZE_FILE)};

//	public static final Path INGREDIENT_FILTER_SIZE_PATH = INGREDIENT_FOLDER_PATH.resolve(INGREDIENT_FILTER_SIZE_FILE);
//	public static final Path RESTRICTION_FILTER_SIZE_PATH = RESTRICTION_FOLDER_PATH.resolve(RESTRICTION_FILTER_SIZE_FILE);
//	public static final Path COMPONENT_FILTER_SIZE_PATH = COMPONENT_FOLDER_PATH.resolve(COMPONENT_FILTER_SIZE_FILE);

}
