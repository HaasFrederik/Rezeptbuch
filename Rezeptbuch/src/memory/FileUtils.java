package memory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backend.comm.MemoryRequest.NewRecipeFileRequest;

//Manages search and manipulation of memory files
public class FileUtils {

////	given sorted(ascending order of length) list of sorted lists(ascending) finds intersection of lists in O(n+m);
////	where n is the combined length of all lists and m the length of the shortest
//	private static <T extends Comparable<T>> List<T> intersectSortedLists(List<List<T>> L) {
//		List<T> intersection = L.removeFirst();
//		for (List<T> l : L) {
//			if (intersection.isEmpty())
//				return intersection;
//			intersection = intersectSortedLists(intersection, l);
//		}
//		return intersection;
//	}

//	finds intersection of two sorted(ascending) lists l1 and l2 in O(|l1| + |l2|)
	private static <T extends Comparable<T>> List<T> intersectSortedLists(List<T> l1, List<T> l2) {
		List<T> intersection = new ArrayList<T>();
		T t2 = l2.get(0);
		;
		for (T t1 : l1) {
			if (t1.compareTo(t2) < 0)
				continue;
			for (int i = l2.indexOf(t2); i < l2.size(); i++) {
				t2 = l2.get(i);
				if (t1.compareTo(t2) > 0)
					continue;
				if (t1.compareTo(t2) < 0)
					break;
				if (t1.compareTo(t2) == 0) {
					intersection.add(t1);
					break;
				}
			}
		}

		return intersection;
	}
//	Consecutively loads filter-Files and joins results, while eliminating duplicates
//	joins file contents and eliminates dupes while preserving sort
	public static List<String> unionSearch(Map<Path, List<String>> folderPathAndFilterNameMap) throws IOException {
		List<String> recipeList = new ArrayList<String>();
		for (Entry<Path, List<String>> e : folderPathAndFilterNameMap.entrySet()) {
			Path folderPath = e.getKey();
			for (String filterName : e.getValue()) {
				Path filePath = folderPath.resolve(filterName + ".txt");
				recipeList.addAll(Files.readAllLines(filePath));
			}
		}
		recipeList.sort(null);
		List<String> union = new ArrayList<String>();
		String current = recipeList.getFirst();
		union.add(current);
		for (String recipe : recipeList) {
			if (current.equals(recipe)) {
				continue;
			}
			current = recipe;
			union.add(current);
		}
		return union;
	}
	
////	joins lists and eliminates duplicates, while upholding sort
//	private static <T extends Comparable<T>> List<T> dupefreeListUnion(List<List<T>> L) {
//		List<T> unionList = L.removeFirst();
//		for (List<T> l : L) {
//			unionList.addAll(l);
//		}
//		Collections.sort(unionList);
//		List<T> unionSet = new ArrayList<T>();
//		T current = unionList.getFirst();
//		unionSet.add(current);
//		for (T t : unionList) {
//			if (current.compareTo(t) == 0) {
//				continue;
//			}
//			current = t;
//			unionSet.add(current);
//		}
//		return unionSet;
//	}
	
//	Consecutively loads filter-Files into (sorted)recipe-lists, starting with the shortest two.
//	Given two lists finds intersection. Consecutive intersections are found between current intersection-list and next larger recipe-list
//	returns the intersection
	public static List<String> intersectionSearch(Map<Path, List<String>> folderPathAndFilterNameMap) throws IOException, Exception {
		LinkedHashMap<Entry<Integer, String>, Path> sortedSelectedFilters = sortSelectedFilters(folderPathAndFilterNameMap);
		Entry<Entry<Integer, String>, Path> firstEntry = sortedSelectedFilters.pollFirstEntry();
		Path firstFilePath = firstEntry.getValue().resolve(firstEntry.getKey().getValue() + ".txt");
		List<String> searchResult = Files.readAllLines(firstFilePath);
		for (Entry<Integer, String> e : sortedSelectedFilters.keySet()) {
//			folder-path in Map, file-name in String + .txt
			Path filePath = sortedSelectedFilters.get(e).resolve(e.getValue() + ".txt");
			List<String> recipeList = Files.readAllLines(filePath);
			searchResult = intersectSortedLists(searchResult, recipeList);
			if (searchResult.isEmpty()) return searchResult;
		}
		return searchResult;
	}

//	sorts all selected filters by value and flips key-value pairing to size-filter
	private static LinkedHashMap<Entry<Integer, String>, Path> sortSelectedFilters(Map<Path, List<String>> folderPathAndFilterNameMap)
			throws IOException, Exception {
		Map<Entry<String, Integer>, Path> allSelectedPairsWithOrigins = selectFilters(folderPathAndFilterNameMap);
		LinkedHashMap<Entry<Integer, String>, Path> sortedSelectedPairs = new LinkedHashMap<Entry<Integer, String>, Path>();
		List<Entry<String, Integer>> pairList = new ArrayList<>(allSelectedPairsWithOrigins.keySet());
		pairList.sort(Entry.comparingByValue());
		for (Entry<String, Integer> e : pairList) {
			sortedSelectedPairs.put(Map.entry(e.getValue(), e.getKey()), allSelectedPairsWithOrigins.get(e));
		}
		return sortedSelectedPairs;
	}

//	selects all pairs for filters given in lists
	private static Map<Entry<String, Integer>,Path> selectFilters(Map<Path, List<String>> folderPathAndFilterNameMap)
			throws IOException, Exception {
		Map<Entry<String, Integer>,Path> allSelectedPairsWithOrigins = new HashMap<Entry<String, Integer>,Path>();
		for (Entry<Path, List<String>> e : folderPathAndFilterNameMap.entrySet()) {
			Map<String, Integer> selectedPairs = selectFilters(e.getValue(), e.getKey());
			for (Entry<String, Integer> p : selectedPairs.entrySet()) {
				allSelectedPairsWithOrigins.put(p, e.getKey());
			}
		}
		return allSelectedPairsWithOrigins;
	}

//	selects pairs for filters given in list
	private static Map<String, Integer> selectFilters(List<String> filters, Path filterFolderPath)
			throws IOException, Exception {
		Map<String, Integer> filterSizePairs = parseFilterSize(filterFolderPath);
		Map<String, Integer> selectedPairs = new HashMap<String, Integer>();
		for (String filter : filters) {
			if (filterSizePairs.containsKey(filter)) {
				selectedPairs.put(filter, filterSizePairs.get(filter));
			}
		}
		return selectedPairs;
	}

//	parses filter-sizes from file
	private static Map<String, Integer> parseFilterSize(Path filterFolderPath) throws IOException, Exception {
		Path filterFilePath = filterFolderPath.resolve(PathLib.FILTER_SIZE_FILE);
		List<String> lines = Files.readAllLines(filterFilePath);
		Map<String, Integer> filterSizeMap = new HashMap<String, Integer>();
		for (String line : lines) {
			String[] pair = line.split(",");
			if (pair.length > 2) {
				throw new Exception(
						"Datei " + filterFilePath.toString() + " korrupt. Zeile " + line + " hat zu viele Einträge.");
			}
			pair[0] = pair[0].trim();
			pair[1] = pair[1].trim();
			filterSizeMap.put(pair[0], Integer.valueOf(pair[1]));
		}
		return filterSizeMap;
	}

//	creates Path for txt file with given name and given folder
	public static Path createTextfilePath(Path folderPath, String fileName) throws Exception {
		if (fileName != null && !fileName.isBlank())
			throw new Exception("Filename blank or null");
		else
			return folderPath.resolve(fileName + ".txt");
	}

//	checks for two alphabetical Strings, whether s1 comes before s2 in alphabetical order (case-insensitive)
	private static boolean comesBefore(String s1, String s2) {
		if (s1.compareToIgnoreCase(s2) < 0)
			return true;
		return false;
	}

	private static List<String> insertRecipeIntoList(List<String> list, String recipeName) {
		for (String s : list) {
			if (comesBefore(recipeName, s))
				list.add(list.indexOf(s), recipeName);
		}
		if (!list.contains(recipeName))
			list.add(recipeName);
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

	public static void fileNewRecipe(NewRecipeFileRequest addReq) throws IOException {
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
		if (Files.exists(addReq.recipePath, LinkOption.NOFOLLOW_LINKS))
			return false;
		if (fileContainsRecipe(PathLib.RECIPE_FOLDER_PATH.resolve("alle.txt"), addReq.recipeName))
			return false;
		if (filesContainRecipe(addReq.restrictionPaths, addReq.recipeName))
			return false;
		if (filesContainRecipe(addReq.ingredientPaths, addReq.recipeName))
			return false;
		if (filesContainRecipe(addReq.componentPaths, addReq.recipeName))
			return false;
		return true;
	}

}
