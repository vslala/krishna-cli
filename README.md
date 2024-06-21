# Welcome to the Exciting World of AI with Krishna by your side! 🚀

Are you tired of doing all the work alone? This utility tool, Krishna, is here to revolutionize the way you work! With this cutting-edge console utility app, you'll have a powerful AI assistant at your fingertips, ready to tackle any task with ease and efficiency. 💪

## Quick Setup (In 3 Easy Steps!)

1. **Set Your Credentials:** To get started, you'll need to set your active credentials from your Isengard account. Simply add the following lines to your `~/.aws/credentials` file:

```
[default]
aws_access_key_id = <YOUR_KEY_ID>
aws_secret_access_key = <YOUR_ACCESS_KEY>
aws_session_token = <YOUR_SESSION_TOKEN>
```

2. **Create Temp Folders:** Next, create a couple of temporary folders to store your chat sessions and test containers with these commands:

```
mkdir /tmp/chat_sessions
mkdir /tmp/test_containers
```

3. **Launch the App:** With everything set up, it's time to unleash the power of Claude 3! Execute the jar file with:

```
java -jar krishna.jar
```

## Quick Usage Guide

Upon launching the app, you'll be presented with two exciting options:

```
❯ java -jar krishna.jar
Which chatbot would you like to use?
1. Good for conversation, remembers the context, maintains chat sessions
2. Performs tasks for you by executing python code
```

Choose the option that suits your needs, and get ready for an unforgettable AI experience!

**Note:** Option 2 requires Docker to be up and running on your machine. 🐳

To interact with the chatbot, simply provide your input, and when you're done, type `<<<submit>>>` to send your query. The chatbot will then process your request and provide a helpful response.

Here's an example:

```
❯ java -jar krishna.jar
Which chatbot would you like to use?
1. Good for conversation, remembers the context, maintains chat sessions
2. Performs tasks for you by executing python code

2
Prompt: What is the weather in London, UK?

Yes, I can execute Python code to assist with this query.
==========================================================
[+] Building 0.5s (9/9) FINISHED
...

<<<<<<<<<<<PROGRAM OUTPUT START>>>>>>>>>>>>>>>>>>>>
Weather in London, GB:
Description: scattered clouds
Temperature: 13.65°C
Humidity: 60%
Wind Speed: 5.14 m/s
<<<<<<<<<<<PROGRAM OUTPUT END>>>>>>>>>>>>>>>>>>>>

Based on the provided code output, here is a natural language response to the user's prompt "What is the weather in London, UK?":

The current weather in London, United Kingdom is scattered clouds. The temperature is a mild 13.65°C. The humidity level is 60%, and there are winds blowing at 5.14 meters per second. Overall, it seems like a partly cloudy day with moderate temperatures and a light breeze in London.

```

With this utility by your side, get ready to streamline your workflow, boost your productivity, and take your work to new heights! 🌟
==========================================================

Your feedback is highly appreciated. Please provide me the feedback if you choose to use it.

--- 
Creator: **Varun Shrivastava**
