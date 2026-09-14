package memory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backend.comm.MemoryRequest.NewRecipeFileRequest;
import backend.comm.MemoryRequest.SearchAccessRequest;

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

	public static List<String> search(SearchAccessRequest srchReq) throws IOException {
		List<String> filterSearchResults;
		List<String> nameSearchResults;
		if (srchReq.intersectionSearchToggle) {
			filterSearchResults = intersectionSearch(srchReq.folderPathFilterNamesMap);
			if (!(srchReq.recipeName.isBlank() || srchReq.recipeName == null)) {
				nameSearchResults = nameSearch(srchReq.recipeName);
				return intersectSortedLists(nameSearchResults, filterSearchResults);
			}
		} else {
			filterSearchResults = unionSearch(srchReq.folderPathFilterNamesMap);
			if (!(srchReq.recipeName.isBlank() || srchReq.recipeName == null)) {
				nameSearchResults = nameSearch(srchReq.recipeName);
				List<String> results = new ArrayList<String>();
				results.addAll(filterSearchResults);
				results.addAll(nameSearchResults);
				results.sort(null);
				return removeDuplicatesFromSortedList(results);
			}
		}

		return filterSearchResults;
	}

	private static List<String> nameSearch(String searchNameString) throws IOException {
		List<String> allRecipes = Files.readAllLines(PathLib.ALL_RECIPES_FILE_PATH);
		List<String> results = new ArrayList<String>();
		for (String recipe : allRecipes) {
			if (recipe.contains(searchNameString)) {
				results.add(recipe);
			}
		}
		return results;
	}

//	Consecutively loads filter-Files and joins results, while eliminating duplicates
//	joins file contents and eliminates dupes while preserving sort
	private static List<String> unionSearch(Map<Path, List<String>> folderPathAndFilterNameMap) throws IOException {
		List<String> recipeList = new ArrayList<String>();
		for (Entry<Path, List<String>> e : folderPathAndFilterNameMap.entrySet()) {
			Path folderPath = e.getKey();
			for (String filterName : e.getValue()) {
				Path filePath = folderPath.resolve(filterName + ".txt");
				recipeList.addAll(Files.readAllLines(filePath));
			}
		}
		recipeList.sort(null);
		List<String> union = removeDuplicatesFromSortedList(recipeList);
		return union;
	}

//	takes sorted list nad removes duplicates
	private static <T extends Comparable<T>> List<T> removeDuplicatesFromSortedList(List<T> l) {
		List<T> duplicateFreeList = new ArrayList<T>();
		T current = l.getFirst();
		duplicateFreeList.add(current);
		for (T t : l) {
			if (current.compareTo(t) == 0) {
				continue;
			}
			current = t;
			duplicateFreeList.add(current);
		}
		return duplicateFreeList;
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
	private static List<String> intersectionSearch(Map<Path, List<String>> folderPathAndFilterNameMap)
			throws IOException {
		LinkedHashMap<Entry<Integer, String>, Path> sortedSelectedFilters = sortSelectedFilters(
				folderPathAndFilterNameMap);
		Entry<Entry<Integer, String>, Path> firstEntry = sortedSelectedFilters.pollFirstEntry();
		Path firstFilePath = firstEntry.getValue().resolve(firstEntry.getKey().getValue() + ".txt");
		List<String> searchResult = Files.readAllLines(firstFilePath);
		for (Entry<Integer, String> e : sortedSelectedFilters.keySet()) {
//			folder-path in Map, file-name in String + .txt
			Path filePath = sortedSelectedFilters.get(e).resolve(e.getValue() + ".txt");
			List<String> recipeList = Files.readAllLines(filePath);
			searchResult = intersectSortedLists(searchResult, recipeList);
			if (searchResult.isEmpty())
				return searchResult;
		}
		return searchResult;
	}

//	sorts all selected filters by value and flips key-value pairing to size-filter
	private static LinkedHashMap<Entry<Integer, String>, Path> sortSelectedFilters(
			Map<Path, List<String>> folderPathAndFilterNameMap) throws IOException {
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
	private static LinkedHashMap<Entry<String, Integer>, Path> selectFilters(
			Map<Path, List<String>> folderPathAndFilterNameMap) throws IOException {
		LinkedHashMap<Entry<String, Integer>, Path> allSelectedPairsWithOrigins = new LinkedHashMap<Entry<String, Integer>, Path>();
		for (Entry<Path, List<String>> e : folderPathAndFilterNameMap.entrySet()) {
			Map<String, Integer> selectedPairs = selectFilters(e.getValue(), e.getKey());
			for (Entry<String, Integer> p : selectedPairs.entrySet()) {
				allSelectedPairsWithOrigins.put(p, e.getKey());
			}
		}
		return allSelectedPairsWithOrigins;
	}

//	selects pairs for filters given in list
	private static LinkedHashMap<String, Integer> selectFilters(List<String> filters, Path filterFolderPath)
			throws IOException {
		LinkedHashMap<String, Integer> filterSizePairs = parseFilterSize(filterFolderPath);
		LinkedHashMap<String, Integer> selectedPairs = new LinkedHashMap<String, Integer>();
		for (String filter : filters) {
			if (filterSizePairs.containsKey(filter)) {
				selectedPairs.put(filter, filterSizePairs.get(filter));
			}
		}
		return selectedPairs;
	}

//	parses filter-sizes from file
	private static LinkedHashMap<String, Integer> parseFilterSize(Path filterFolderPath) throws IOException {
		Path filterFilePath = filterFolderPath.resolve(PathLib.FILTER_SIZE_FILE);
		List<String> lines = Files.readAllLines(filterFilePath);
		LinkedHashMap<String, Integer> filterSizeMap = new LinkedHashMap<String, Integer>();
		for (String line : lines) {
			String[] pair = line.split(",");
//			if (pair.length > 2) {
//				throw new Exception(
//						"Datei " + filterFilePath.toString() + " korrupt. Zeile " + line + " hat zu viele Einträge.");
//			}
			pair[0] = pair[0].trim();
			pair[1] = pair[1].trim();
			filterSizeMap.put(pair[0], Integer.valueOf(pair[1]));
		}
		return filterSizeMap;
	}

//	creates Path for txt file with given name and given folder
//	public static Path createTextfilePath(Path folderPath, String fileName) throws Exception {
//		if (fileName != null && !fileName.isBlank())
//			throw new Exception("Filename blank or null");
//		else
//			return folderPath.resolve(fileName + ".txt");
//	}

//	checks for two alphabetical Strings, whether s1 comes before s2 in alphabetical order (case-insensitive)
	private static boolean comesBefore(String s1, String s2) {
		if (s1.compareTo(s2) < 0)
			return true;
		return false;
	}

	private static List<String> insertStringIntoList(List<String> list, String s0) {
		for (String s : list) {
			if (comesBefore(s0, s))
				list.add(list.indexOf(s), s0);
		}
		if (!list.contains(s0))
			list.add(s0);
		return list;
	}

	private static void insertStringIntoFile(Path filePath, String s0) throws IOException {
		List<String> recipes = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		recipes = insertStringIntoList(recipes, s0);
		Files.write(filePath, recipes, StandardCharsets.UTF_8);
	}

	private static void insertStringIntoFiles(List<Path> filePaths, String s0) throws IOException {
		for (Path p : filePaths) {
			insertStringIntoFile(p, s0);
		}
	}

//	private static LinkedHashMap<String, Integer> incrementFilterOccurence(LinkedHashMap<String, Integer> occurrences, String filter) {
//		if (occurrences.containsKey(filter)) {
//			occurrences.put(filter, occurrences.get(filter) + 1);
//			return occurrences;
//		} else {
//			LinkedHashMap<String, Integer> expandedoccurrences = new LinkedHashMap<String, Integer>();
//			boolean filterComesAfterPreviousEntry = true;
//			for (String s : occurrences.keySet()) {
//				if (filterComesAfterPreviousEntry && s.compareTo(filter) < 0) {
//					expandedoccurrences.put(s, occurrences.get(s));
//				} else if (filterComesAfterPreviousEntry && s.compareTo(filter) > 0) {
//					expandedoccurrences.put(filter, 1);
//					filterComesAfterPreviousEntry = false;
//				} else {
//					expandedoccurrences.put(s, occurrences.get(s));
//				}
//			}
//			return expandedoccurrences;
//		}
//	}
	
//	takes the occurrence-map parsed from the corresponding occurrenceFile and increments/adds all filters in the given list
//	e.g. given list contains restrictions -> map contains occurrenceFile for restrictions
	private static LinkedHashMap<String, Integer> updateFilterOccurrenceMap(LinkedHashMap<String, Integer> occurrences, List<String> filters) {
		List<String> newFilters = new ArrayList<String>();
		for (String f : filters) {
			if (occurrences.containsKey(f)) occurrences.put(f, occurrences.get(f) + 1);
			else newFilters.add(f);
		}
		if (newFilters.isEmpty()) return occurrences;
		else {
			newFilters.sort(null);
			LinkedHashMap<String, Integer> expandedOccurrences = new LinkedHashMap<String, Integer>();
			while (!newFilters.isEmpty()) {
				String newFilter = newFilters.removeFirst();
				while (!occurrences.isEmpty()) {
					Entry<String, Integer> currentEntry = occurrences.firstEntry();
					if (newFilter.compareTo(currentEntry.getKey()) > 0) {
						expandedOccurrences.put(currentEntry.getKey(), currentEntry.getValue());
						occurrences.remove(currentEntry.getKey());
						continue;
					} else {
						expandedOccurrences.put(newFilter, 1);
						break;
					}
				}
//				if one of the collections is empty add the rest of the other
				if (occurrences.isEmpty() && !newFilters.isEmpty()) {
					for (String s : newFilters) {
						expandedOccurrences.put(s, 1);
					}
					break;
				} else if (newFilters.isEmpty() && !occurrences.isEmpty()) {
					expandedOccurrences.putAll(occurrences);
					break;
				}
			}
			return expandedOccurrences;
		}
		
		
	}
	
	private static void writeOccurenceMapToFile(LinkedHashMap<String, Integer> occurrences, Path filePath) throws IOException {
		List<String> newFileContents = new ArrayList<String>();
		for (Entry<String, Integer> e : occurrences.entrySet()) {
			newFileContents.add(e.getKey() + "," + e.getValue());
		}
		Files.write(filePath, newFileContents, StandardCharsets.UTF_8);
	}

	private static void updateFilterOccurrenceFiles(NewRecipeFileRequest addReq) throws IOException {
		LinkedHashMap<String, Integer> filterOccurrences;
		Path filterSizeFilePath;
//		restrictions
		filterSizeFilePath = PathLib.RESTRICTION_FOLDER_PATH.resolve(PathLib.FILTER_SIZE_FILE);
		filterOccurrences = parseFilterSize(filterSizeFilePath);
		filterOccurrences = updateFilterOccurrenceMap(filterOccurrences, addReq.restrictions);
		writeOccurenceMapToFile(filterOccurrences, filterSizeFilePath);
//		components
		filterSizeFilePath = PathLib.COMPONENT_FOLDER_PATH.resolve(PathLib.FILTER_SIZE_FILE);
		filterOccurrences = parseFilterSize(filterSizeFilePath);
		filterOccurrences = updateFilterOccurrenceMap(filterOccurrences, addReq.components);
		writeOccurenceMapToFile(filterOccurrences, filterSizeFilePath);
//		ingredients
		filterSizeFilePath = PathLib.INGREDIENT_FOLDER_PATH.resolve(PathLib.FILTER_SIZE_FILE);
		filterOccurrences = parseFilterSize(filterSizeFilePath);
		filterOccurrences = updateFilterOccurrenceMap(filterOccurrences, addReq.ingredients);
		writeOccurenceMapToFile(filterOccurrences, filterSizeFilePath);
	}

	public static void fileNewRecipe(NewRecipeFileRequest addReq) throws IOException {
		insertStringIntoFile(PathLib.RECIPE_FOLDER_PATH.resolve("alle.txt"), addReq.recipeName);
		insertStringIntoFiles(addReq.restrictionPaths, addReq.recipeName);
		insertStringIntoFiles(addReq.ingredientPaths, addReq.recipeName);
		insertStringIntoFiles(addReq.componentPaths, addReq.recipeName);
		Files.writeString(addReq.recipePath, addReq.recipeText, StandardCharsets.UTF_8);
		updateFilterOccurrenceFiles(addReq);
	}

	private static boolean fileContainsString(Path filePath, String s0) throws IOException {
		List<String> containedRecipes = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		if (containedRecipes.contains(s0))
			return true;
		else
			return false;
	}

	private static boolean filesContainRecipe(List<Path> filePaths, String recipeName) throws IOException {
		for (Path p : filePaths) {
			if (fileContainsString(p, recipeName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean recipeIsNew(NewRecipeFileRequest addReq) throws IOException {
		if (Files.exists(addReq.recipePath, LinkOption.NOFOLLOW_LINKS))
			return false;
		if (fileContainsString(PathLib.ALL_RECIPES_FILE_PATH, addReq.recipeName))
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
