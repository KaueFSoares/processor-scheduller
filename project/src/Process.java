public record Process(String id, int burst, int priority, int time) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Process))
            return false;

        return ((Process) obj).id().equals(this.id());
    }
}
