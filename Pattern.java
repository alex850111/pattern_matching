package patternMatching.loop;

import java.util.ArrayList;

import patternMatching.AbusivePatternMatching;

public class Pattern {
	private String[] target;
	private String rewriteContents = "嗶----!!";
	private int currentTargetIndex;
	private String[] matchedSubString;
	private int rewriteStartIndex, rewriteEndIndex;
	
	
	public Pattern(String patternString) {
		String temp[] = patternString.split("\t");
		target = temp[0].trim().split(" ");
		rewriteContents = temp[1].replaceAll("\"", "");
		
		matchedSubString = new String[target.length];
		for(int i = 0; i < target.length; i++) {
			if(target[i].startsWith("|")){
				rewriteStartIndex = i;
				target[i] = target[i].substring(1);
			}
			if(target[i].endsWith("|")){
				rewriteEndIndex = i;
				target[i] = target[i].substring(0, target[i].length() - 1);				
			}
		}
		
		currentTargetIndex = 0;
	}
	
	public boolean match(String s){
		String matchingSentence = s.substring(0);
		int i = currentTargetIndex;
		boolean matchFlag = false;
		
		while (i < target.length) {
			if (target[i].equals("@") || target[i].equals("*")) {
				if (isPatternEnd(i)){	
					matchFlag = target[i].equals("*") || matchingSentence.isEmpty();
					if(matchFlag)
						matchedSubString[i] = matchingSentence;
					break;
				}
				//OK
				for (int sIndex = target[i].equals("@") ? 1 : 0; sIndex < matchingSentence.length(); sIndex++) {
					currentTargetIndex = i + 1;
					if (match(matchingSentence.substring(sIndex))) {
						matchedSubString[i] = matchingSentence.substring(0, sIndex);
						currentTargetIndex = 0;
						return true;
					}
				}
				currentTargetIndex = 0;
				return false;
			} else if (target[i].startsWith("~")) {
				ArrayList<String> dictionary = AbusivePatternMatching.dictionarys.get(target[i].substring(1));
				int wordIndex;
				for (wordIndex = 0; wordIndex < dictionary.size(); wordIndex++) {
					String word = dictionary.get(wordIndex);
					if (matchingSentence.startsWith(word)) {
						matchedSubString[i] = word;
						if (isPatternEnd(i)){
							matchFlag = true;
						}
						else {
							matchingSentence = matchingSentence.substring(word.length());
						}
						i++;
						break;
					}
				}
				if(wordIndex == dictionary.size())
					return false;
				//OK
			} else if(target[i].startsWith("[")) {
				System.out.println("matching optional");
				String target_temp = target[i].substring(1, target[i].length() - 1);
				if(target_temp.startsWith("~")) {
					ArrayList<String> dictionary = AbusivePatternMatching.dictionarys.get(target[i].substring(1));
					for (String word : dictionary) {
						if (matchingSentence.startsWith(word)) {
							matchedSubString[i] = word;						
							matchingSentence = matchingSentence.substring(word.length());
							break;
						}
					}
				} else {
					if (matchingSentence.startsWith(target_temp)) {
						matchedSubString[i] = target_temp;
						matchingSentence = matchingSentence.substring(target_temp.length());
					}
				}
				matchFlag = true;
				i++;
			} else {
				if (matchingSentence.startsWith(target[i])) {
					matchFlag = true;
					matchedSubString[i] = target[i];
					matchingSentence = matchingSentence.substring(target[i].length());
					i++;
				} else
					return false;
			} 
			//OK
		}
		return matchFlag;
	}
	
	private boolean isPatternEnd(int index){
		return index == (target.length - 1);
	}
	
	public String getPatternString(){
		String ss = "";
		for(String s : target)
			ss += s + " ";
		return ss;
	}
	
	public String getRewriteSentence() {
		String sentence = "";
		for(int i = 0; i < rewriteStartIndex; i++)
			sentence += matchedSubString[i];
		sentence += rewriteContents;
		
		for(int i = rewriteEndIndex + 1; i < matchedSubString.length; i++)
			sentence += matchedSubString[i];
		return sentence;
	}
}
