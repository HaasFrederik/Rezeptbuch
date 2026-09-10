package backend.comm;

import java.util.List;

import frontend.GUIState;

public class UserRequest extends Comm {

	public class SearchRequest extends UserRequest {
		public String searchName;
		public List<String> searchRestrictions;
		public List<String> searchIngredients;
		public List<String> searchComponents;
		public boolean conjunctiveSearchToggle;
		
		public SearchRequest(GUIState gui) {
			searchName = gui.searchName;
			searchRestrictions = gui.searchRestrictions;
			searchIngredients = gui.searchIngredients;
			searchComponents = gui.searchComponents;
			conjunctiveSearchToggle = gui.conjunctiveSearchToggle;
		}
	}

	public class EditRequest extends UserRequest {

	}

	public class NewRecipeRequest extends UserRequest {
		public String recipeName;
		public List<String> restrictions;
		public String portions;
		public List<String> ingredients;
		public List<String> amounts;
		public List<String> components;
		public String recipeText;

		public NewRecipeRequest(GUIState gui) {
			recipeName = gui.newRecipeName;
			restrictions = gui.newRestrictions;
			portions = gui.newPortions;
			ingredients = gui.newIngredients;
			amounts = gui.newAmounts;
			components = gui.newComponents;
			recipeText = gui.newRecipeText;
		}
	}

//	TODO Loop over backend and memory done, frontend missing (beginning and end of loop)
	public class ReadRequest extends UserRequest {
		public String recipeName;

		public ReadRequest(GUIState gui) {
			recipeName = gui.readRecipeName;
		}
	}

}
