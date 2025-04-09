## Example 1: Generate a C# Code Snippet

```sh
krishna -p "raw_query" -prompt "Write C# code to generate the sum of all the odd numbers present in the even indices"
```

## Example 2: Analyze Claims 


```sh
krishna -p "raw_query" -prompt "Write C# code to generate the sum of all the odd numbers present in the even indices" > tmp_output.dat && cat tmp_output.dat| krishna -p "analyze_claims"
```

## Example 3: Code Analysis

```sh 
krishna -p "raw_query" -prompt "Write C# code to generate the sum of all the odd numbers present in the even indices" > tmp_output.dat && cat tmp_output.dat| krishna -p "analyze_code"
```

## Exmaple 4: Language Translation

```sh 
echo ""A los ingenieros les gusta resolver problemas. Si no hay problemas disponibles, crearán sus propios problemas." - Scott Adams" | krishna -p "raw_query" -prompt "translate to english"
```

## Example 5: Extract Insights from white paper with extended prompts

```sh
pdftotext ~/Documents/bemyaficionado/NIPS-2017-attention-is-all-you-need-Paper.pdf pdftext.dat && cat pdftext.dat| krishna -p "extract_insights" -prompt "In addition to the previous insights, also explain the abbreviations and definitions of key entities under ADDITIONAL EXPLANATION section"
```

## Example 6: Summarise Paper with extended prompt 

```sh 
pdftotext ~/Documents/bemyaficionado/NIPS-2017-attention-is-all-you-need-Paper.pdf pdftext.dat && cat pdftext.dat| krishna -p "summarize_paper" -prompt "In addition to the previous insights, also explain the abbreviations and definitions of key entities under ADDITIONAL EXPLANATION section"
```

## Example 7: Create Git Diff Commit
```sh
git diff > git_diff.dat
git show HEAD > git_show_head.dat
```
```sh 
echo "Git Diff: $(cat git_diff.dat ); git show HEAD: $(cat git_show_head.dat )" | krishna -p "create_git_diff_commit"
```

## Example 8: Summarise Git Diff 

```sh 
echo "Git Diff: $(cat git_diff.dat )" | krishna -p "summarize_git_diff"
```

## Example 9: Format Text

```sh 
echo '{"name":"John","age":30,"city":"New York","children":[{"name":"Anna","age":10},{"name":"Ella","age":5}]}' | krishna -p "raw_query" -prompt "format provided json"
```


## Example 10: Use Agents to extend the LLM capability by providing code development environment

```sh 
# Find the latest amazon share price

krishna agent -lang python -prompt "Find the latest share price of Amazon right now? Do not scrape the webpage rather Use the yahoo yfinance libraries to fetch the results if needed."
```
