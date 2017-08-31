package nl.sjtek.control.core.assistant

data class AssistantResponse(val speech: String, val displayText: String = speech, val source: String = "Sjtek")