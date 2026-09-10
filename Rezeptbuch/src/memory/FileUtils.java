package memory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

import backend.comm.MemoryRequest.NewRecipeFileRequest;

//Manages search and manipulation of memory files
public class FileUtils {

	public static Path createTextfilePath(Path folderPath, String fileName) throws Exception {
		if (fileName != null && !fileName.isBlank()) throw new Exception("Filename blank or null");
		else return folderPath.resolve(fileName + ".txt");		
	}

//	checks for two numerical Strings, whether s1 comes before s2 in alphabetical order (case-insensitive)
	private static boolean comesBefore(String s1, String s2) {
		if (s1.compareToIgnoreCase(s2) < 0) return true;
		return false;
	}
	
	private static List<String> insertRecipeIntoList(List<String> list, String recipeName) {
		for (String s : list) {
			if (comesBefore(recipeName, s))	list.add(list.indexOf(s), recipeName);
		}
		if (!list.contains(recipeName)) list.add(recipeName);
		return list;
	}
	
	private static void insertRecipeIntoFile(Path filePath, String recipeName) throws IOException {
		List<String> recipes = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		recipes = insertRecipeIntoList(recipes, recipeName);
		Files.write(filePath, recipes, StandardCharsets.UTF_8);
	}
	
	private static void insertRecipeIntoFiles(List<Path> filePaths, String recipeName) throws IOException {
		for (Path p : filePaths) {
			insertRecipeIntoFile(p, recipeName);
		}
	}
	
	public static void fileNewRecipe(NewRecipeFileRequest addReq) throws IOException{
		insertRecipeIntoFile(PathLib.RECIPE_FOLDER_PATH.resolve("alle.txt"), addReq.recipeName);
		insertRecipeIntoFiles(addReq.restrictionPaths, addReq.recipeName);
		insertRecipeIntoFiles(addReq.ingredientPaths, addReq.recipeName);
		insertRecipeIntoFiles(addReq.componentPaths, addReq.recipeName);
		Files.writeString(addReq.recipePath, addReq.recipeText, StandardCharsets.UTF_8);
	}
	
	private static boolean fileContainsRecipe(Path filePath, String recipeName) throws IOException {
		List<String> containedRecipes = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		if (containedRecipes.contains(recipeName))
			return true;
		else
			return false;
	}

	private static boolean filesContainRecipe(List<Path> filePaths, String recipeName) throws IOException {
		for (Path p : filePaths) {
			if (fileContainsRecipe(p, recipeName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean recipeIsNew(NewRecipeFileRequest addReq) throws IOException {
		if (Files.exists(addReq.recipePath, LinkOption.NOFOLLOW_LINKS)) return false;
		if (fileContainsRecipe(PathLib.RECIPE_FOLDER_PATH.resolve("alle.txt"), addReq.recipeName)) return false;
		if (filesContainRecipe(addReq.restrictionPaths, addReq.recipeName)) return false;
		if (filesContainRecipe(addReq.ingredientPaths, addReq.recipeName)) return false;
		if (filesContainRecipe(addReq.componentPaths, addReq.recipeName)) return false;
		return true;
	}
	
}
