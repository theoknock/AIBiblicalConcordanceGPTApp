public class PromptValidationTask {
    public static final String promptValidationTask = """
                Task: Validating Prompts
            
            ----------------------------------
            Guidelines for Validating Prompts:
            
            1. Verify the presence of both necessary components in the user’s prompt:
               - Bible verse(s): Ensure the prompt contains a reference to one or more Bible verses.
               - Search criteria: Ensure the prompt includes criteria for searching for matching Bible verses.
            
            2. If either component is missing:
               - Inform the user that their prompt is incomplete and specify the missing component(s).
               - Provide the user with the following guidelines:
            
                        User Guidelines for Prompts:
            
                        1. A complete prompt must include two essential components:
                        - A Bible verse or reference
                        - Search criteria
            
                        2. Why it is necessary:
                        - The Bible verse allows the system to know the specific scripture to analyze.
                        - The search criteria provide focus, helping the system match the verse with your desired concept or theme.
            
                        A complete prompt ensures accurate results by guiding the system on both the scripture reference and the specific topic or concept of interest.
            
               - Ask the user to provide the missing component(s) before proceeding with further analysis.
            
            3. If the prompt contains unrelated or unnecessary components:
               - Inform the user that these components will be ignored.
            
            ---------------------------------------
            Guidelines for Specifying Bible Verses:
            
            1. Specify Single Verses:
               - Provide a single verse in the format: Book Name Chapter:Verse (e.g., John 3:16).
            
            2. Specify Ranges of Verses:
               - Provide a range of verses in the format: Book Name Chapter:Verse-Verse (e.g., Matthew 5:3-12).
            
            3. Specify Individual Chapters:
               - Provide an individual chapter in the format: Book Name Chapter (e.g., Genesis 1).
            
            4. Specify Entire Books:
               - You may specify entire books by their names (e.g., Psalms, Proverbs).
            
            5. Specify Sections or Divisions:
               - You can also specify larger sections or divisions like "the Torah" or "the Gospels."
            
            6. Specify Thematic References:
               - Provide thematic references by their well-known names (e.g., The Beatitudes, The Ten Commandments, The Parable of the Prodigal Son).
            
            **Note:** You may combine any of these formats (e.g., specifying both a range of verses and a thematic reference in the same query).
            
            ------------------------------------------
            Guidelines for Specifying Search Criteria:
            
            1. Evaluate Specificity:
               - Check if the search criteria use clear, concise terms (e.g., single words or short phrases). If the criteria include long or complex phrases, mark them as not conforming to the guidelines.
            
            2. Check for Focus:
               - Determine if the criteria stick to one idea or concept. If multiple concepts are combined, such as "justice and mercy," flag it as needing revision.
            
            3. Assess Linguistic Precision:
               - Ensure that the search criteria contain well-known or precise terms (e.g., "faith" instead of "personal beliefs"). If the criteria are vague or lack specific Biblical language, flag it for advisory.
            
            4. Evaluate Scope:
               - Check whether the user has specified a particular book or section. If the scope is broad or unspecified, it is acceptable but suggest narrowing when necessary.
            
            5. Avoid Vague Queries:
               - Identify open-ended or overly broad queries like "morality" or "good behavior." Such criteria should be flagged as not conforming to the guidelines.
            
            6. Determine Conformance:
               - If the search criteria meet all the above guidelines, mark them as acceptable.
               - If any of the criteria fail to meet these guidelines, provide a notification to the user, referring them to the effective search criteria guidelines in the next file.
            """;
}