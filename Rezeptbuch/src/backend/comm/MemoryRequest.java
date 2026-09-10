package backend.comm;

import java.nio.file.Path;
import java.util.List;

import backend.comm.UserRequest.NewRecipeRequest;
import backend.comm.UserRequest.SearchRequest;
import memory.PathLib;

public class MemoryRequest extends Comm {

	public static class ReadAccessRequest extends MemoryRequest {
		public Path recipePath;
		
		public ReadAccessRequest (String recipeName) {
			recipePath = PathLib.RECIPE_FOLDER_PATH.resolve(recipeName + ".txt");
		}
	}

	public static class EditAccessRequest extends MemoryRequest{

	}

	public static class NewRecipeFileRequest extends MemoryRequest{
		public String recipeName;
		public String recipeText = "";
		
		public Path recipePath;
		public List<Path> restrictionPaths;
		public List<Path> ingredientPaths;
		public List<Path> componentPaths;

		public NewRecipeFileRequest(NewRecipeRequest addReq) {
			this.recipeName = addReq.recipeName;
			recipePath = PathLib.RECIPE_FOLDER_PATH.resolve(recipeName + ".txt");
			
			for (String restriction : addReq.restrictions) {
				restrictionPaths.add(PathLib.RESTRICTION_FOLDER_PATH.resolve(restriction + ".txt"));
				if (addReq.restrictions.indexOf(restriction) < addReq.restrictions.size() - 1) {
					recipeText += restriction + ", ";
				} else {
					recipeText += restriction + "\n\n";
				}
			}
			
			recipeText += "Anzahl Personen: " + addReq.portions + "\n\n";
			
			recipeText += "Zutaten:\n";		
			for (String ingredient : addReq.ingredients) {
				ingredientPaths.add(PathLib.INGREDIENT_FOLDER_PATH.resolve(ingredient + ".txt"));
				recipeText += "- " + ingredient + ", " + addReq.amounts.get(addReq.ingredients.indexOf(ingredient)) + "\n";
			}
			recipeText += "\n";
			
			recipeText += "Komponenten:\n";
			for (String component : addReq.components) {
				componentPaths.add(PathLib.COMPONENT_FOLDER_PATH.resolve(component + ".txt"));
				recipeText += "- " + component + "\n";
			}
			recipeText += "\n";
			
			recipeText += addReq.recipeText;
		}	
	}
	
	public static class SearchAccessRequest extends MemoryRequest {

		public SearchAccessRequest(SearchRequest srchReq) {
//			TODO search dem files
			
//			all filters must be satisfied (&&)
			if(srchReq.conjunctiveSearchToggle) {
//				create all necessary filter-file-paths
				
			}
//			any recipe satisfying one filter (||)
			else {
				
			}
		}
		
	}

}
