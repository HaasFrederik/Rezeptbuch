package backend.handle;

import backend.comm.*;
import backend.comm.MemoryRequest.*;
import backend.comm.MemoryResponse.*;
import backend.comm.UserRequest.*;
import backend.comm.UserResponse.*;
import frontend.UserResponseHandler;
import memory.MemoryRequestHandler;

public class MainHandler {
	private static int commTypeFlag = -1;
	private static final int SEARCH_REQUEST = 0;
	private static final int READ_REQUEST = 1;
	private static final int EDIT_REQUEST = 2;
	private static final int ADD_REQUEST = 3;

	private static UserRequest userRequest = null;
	private static MemoryResponse memoryResponse = null;

	private class UserRequestHandler {

		private static MemoryRequest handle(UserRequest usrReq) {
			userRequest = usrReq;
			MemoryRequest memReq = null;
			switch (usrReq) {

			case SearchRequest srchReq -> {
				memReq = new SearchAccessRequest(srchReq);
				commTypeFlag = SEARCH_REQUEST;
			}
			case ReadRequest readReq -> {
				memReq = new ReadAccessRequest(readReq.recipeName);
				commTypeFlag = READ_REQUEST;
			}

			case EditRequest editReq -> {
				memReq = new EditAccessRequest();
				commTypeFlag = EDIT_REQUEST;
			}
			case NewRecipeRequest addReq -> {
				memReq = new NewRecipeFileRequest(addReq);
				commTypeFlag = ADD_REQUEST;
			}
			default -> {
				System.out.println("UserRequest-Type not recognised in MainHandler.UserRequestHandler.handle");
			}
			}
			return memReq;

		}

	}

	private class MemoryResponseHandler {

		private static UserResponse handle(MemoryResponse memResp) {
			memoryResponse = memResp;
			UserResponse usrResp = null;
			switch (memResp) {
			case ReadAccessResponse readAccResp -> {
				usrResp = new ReadResponse(readAccResp.recipeText);
			}
			case EditAccessResponse editAccResp -> {
				usrResp = new EditResponse();
			}
			case NewRecipeSuccess addSucc -> {
				usrResp = new NewRecipeResponse(addSucc);
			}
			default -> {

			}
			}
			return usrResp;

		}

	}

	public static void handle(Comm c) {
		switch (c) {

		case UserRequest usrReq -> {
			MemoryRequest memReq = UserRequestHandler.handle(usrReq);
			MemoryRequestHandler.handle(memReq);
		}
		case MemoryResponse memResp -> {
			UserResponse usrResp = MemoryResponseHandler.handle(memResp);
			UserResponseHandler.handle(usrResp);
		}
		default -> {
			System.out.println("Comm-Obj. not recognised in MainHandler.handle");
		}
		}

	}

}
