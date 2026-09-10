package backend.comm;

public class MemoryResponse extends Comm{

	public static class EditAccessResponse extends MemoryResponse {
		
	}
	
	public static class NewRecipeSuccess extends MemoryResponse {
		
		public boolean fileCreationSuccessful;
		public String errorMessage;
		
		public NewRecipeSuccess(boolean succ) {
			fileCreationSuccessful = succ;
		}
		
		public NewRecipeSuccess(boolean succ, String msg) {
			fileCreationSuccessful = succ;
			errorMessage = msg;
		}
		
	}
	
	public static class ReadAccessResponse extends MemoryResponse {
		
		public String recipeText;
		
		public ReadAccessResponse (String recipeText) {
			this.recipeText = recipeText;
		}
		
	}
	
	public static class SearchAccessResponse extends MemoryResponse {
		
	}
	
}
