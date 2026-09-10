package memory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import backend.comm.MemoryRequest;
import backend.comm.MemoryRequest.*;
import backend.comm.MemoryResponse;
import backend.comm.MemoryResponse.NewRecipeSuccess;
import backend.comm.MemoryResponse.ReadAccessResponse;
import backend.handle.MainHandler;

public class MemoryRequestHandler {

	public static void handle(MemoryRequest memReq) {
		MemoryResponse memResp = null;
		switch (memReq) {
		case ReadAccessRequest readReq -> {
			String recipeText = "oops";
			try {
				recipeText = Files.readString(readReq.recipePath, StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error loading file under " + readReq.recipePath.toString());
			}
			memResp = new ReadAccessResponse(recipeText);
		}
		case NewRecipeFileRequest addReq -> {
			boolean isNew = false;
			try {
				if (FileUtils.recipeIsNew(addReq)) {
					isNew = true;
				}
			} catch (IOException e) {
				memResp = new NewRecipeSuccess(false, "Error checking if new recipe is new");
				System.out.println("Error checking if new recipe is new");
				e.printStackTrace();
				break;
			}
			if (isNew) {
				try {
					FileUtils.fileNewRecipe(addReq);
					memResp = new NewRecipeSuccess(true);
				} catch (IOException e) {
					memResp = new NewRecipeSuccess(false, "Error writing new recipe");
					System.out.println("Error writing new recipe");
					e.printStackTrace();
					break;
				}
			} else {
//				TODO Recipe not new, mentioned in at least one of the affected files
//				create memoryResonse that encapsulates that
				memResp = new NewRecipeSuccess(false, "Recipe already mentioned in files. Consider validating files.");
			}
			

		}
		default -> {
			System.out.println("MemoryRequest-type not recognised by MemoryRequestHandler");
		}
		}
		MainHandler.handle(memResp);

	}

	

}
