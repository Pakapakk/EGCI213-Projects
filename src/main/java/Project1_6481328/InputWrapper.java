package Project1_6481328;

class InputHandler {
    private String [] arguments;
    private String originalInput;

    public InputHandler(String [] arguments, String originalInput) {
        this.arguments = arguments;
        this.originalInput = originalInput;
    }

    public String [] getArgumentsString() { return this.arguments; }
    public String getOriginalInput() { return this.originalInput; }
    public boolean isComment() { return originalInput.trim().startsWith("#"); }
    public boolean isBlank() { return originalInput.trim().isEmpty(); }
}
