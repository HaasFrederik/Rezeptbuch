package backend.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.comm.MemoryResponse.NewRecipeSuccess;

public class UserResponse extends Comm {

	public static class SearchResponse extends UserResponse {

	}

	public static class EditResponse extends UserResponse {

	}

	public static class NewRecipeResponse extends UserResponse {
		public boolean success;
		public String errorMessage;
		
		public NewRecipeResponse(NewRecipeSuccess nrs) {
			success = nrs.fileCreationSuccessful;
			errorMessage = nrs.errorMessage;
		}
	}

	public static class ReadResponse extends UserResponse {
		public String restrictions = "";
		public String portions = "";
		public String ingredients = "";
		public String components = "";
		public String directions = "";
		public String errorMessage = "";
		
		private Pattern restrictionsPattern = Pattern.compile(".*?\\n\\n");
		private Pattern portionsPattern = Pattern.compile("Anzahl Personen: [0-9]+\\n\\n");
		private Pattern ingredientsPattern = Pattern.compile("Zutaten:\\n(- \\D+?, \\d+? \\D+?\\n)+?\\n");
		private Pattern componentsPattern = Pattern.compile("Komponenten:\\n(- \\D+?\\n)+?\\n");
//		private Pattern directionsPattern = Pattern.compile("Vorbereitung:\\n(.+?\\n)+?\\nZubereitung:\\n(.+?\\n)+");
		
		public ReadResponse (String recipeText) {
			List<String> sections = parseRecipe(new Pattern[]{restrictionsPattern, portionsPattern, ingredientsPattern, componentsPattern}, recipeText);
			if (!sections.isEmpty()) {
				portions = sections.get(0);
				ingredients = sections.get(1);
				components = sections.get(2);
				directions = sections.get(3);
			} else {
				errorMessage = "Fehler beim Auslesen des Rezepts.";
			}
		}
		
		private List<String> parseRecipe(Pattern[] patterns, String textToParse) {
			List<String> sections = new ArrayList<String>();
			for (Pattern pattern : patterns) {
				Matcher matcher = pattern.matcher(textToParse);
				if (matcher.matches()) {
					sections.add(matcher.group());
					textToParse = matcher.replaceFirst("");
				} else return new ArrayList<String>();
			}
			sections.add(textToParse);
			return sections;
		}
		
//		TODO check "components" for potential recursive recipe-parsing, if done redo code for writing of "directions" 
		
	}

}
