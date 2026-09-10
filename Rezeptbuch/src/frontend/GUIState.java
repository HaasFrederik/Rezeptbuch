package frontend;

import java.util.List;

public class GUIState {

//	TODO all Data put into GUIState MUST NOT be null or empty Strings
	
//	Fields necessary for search
	public String searchName;
	public List<String> searchRestrictions;
	public List<String> searchIngredients;
	public List<String> searchComponents;
	public boolean conjunctiveSearchToggle;
	
	public GUIState(List<String> srchRestr, List<String> srchIngr, List<String> srchComp, boolean conjTggl) {
		searchRestrictions = srchRestr;
		searchIngredients = srchIngr;
		searchComponents = srchComp;
		conjunctiveSearchToggle = conjTggl;
	}
	
	public GUIState(String srchName, boolean srchTggl) {
		searchName = srchName;
		conjunctiveSearchToggle = srchTggl;
	}
	
//	Fields necessary for reading content of a recipe
	public String readRecipeName;
	
	public GUIState(String readRecipeName) {
		this.readRecipeName = readRecipeName;
	}
	
//	Fields necessary for creation of new recipe
	public String newRecipeName;
	public List<String> newRestrictions;
	public String newPortions;
	public List<String> newIngredients;
	public List<String> newAmounts;
	public List<String> newComponents;
	public String newRecipeText;
	
	public GUIState(String recipeName, List<String> restrictions, String portions, List<String> ingredients,
			List<String> amounts, List<String> components, String recipeText) {
		this.newRecipeName = recipeName;
		this.newRestrictions = restrictions;
		this.newPortions = portions;
		this.newIngredients = ingredients;
		this.newAmounts = amounts;
		this.newComponents = components;
		this.newRecipeText = recipeText;
	}
	
//	Fields necessary for editing of existing recipe
	
}
