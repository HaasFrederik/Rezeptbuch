package memory;

import java.nio.file.Path;

public class PathLib {

	private static final String BASE_FOLDER_NAME = "Rezeptbuch";
	private static final String RECIPE_FOLDER_NAME = "Rezepte";
	private static final String FILTER_FOLDER_NAME = "Filter";
	private static final String INGREDIENT_FOLDER_NAME = "Zutaten";
	private static final String RESTRICTION_FOLDER_NAME = "Restriktionen";
	private static final String COMPONENT_FOLDER_NAME = "Komponenten";
	
	public static final Path BASE_FOLDER_PATH = Path.of(BASE_FOLDER_NAME);
	public static final Path RECIPE_FOLDER_PATH = BASE_FOLDER_PATH.resolve(RECIPE_FOLDER_NAME);
	public static final Path FILTER_FOLDER_PATH = BASE_FOLDER_PATH.resolve(FILTER_FOLDER_NAME);
	public static final Path INGREDIENT_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(INGREDIENT_FOLDER_NAME);
	public static final Path RESTRICTION_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(RESTRICTION_FOLDER_NAME);
	public static final Path COMPONENT_FOLDER_PATH = FILTER_FOLDER_PATH.resolve(COMPONENT_FOLDER_NAME);

	
	
}
