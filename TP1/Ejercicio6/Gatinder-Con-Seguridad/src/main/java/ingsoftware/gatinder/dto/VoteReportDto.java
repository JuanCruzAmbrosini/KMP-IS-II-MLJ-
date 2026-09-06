package ingsoftware.gatinder.dto;

public class VoteReportDto {
    private final String firstName;
    private final String lastName;
    private final String petName;
    private final long voteCount;

    public VoteReportDto(String firstName, String lastName, String petName, long voteCount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.petName = petName;
        this.voteCount = voteCount;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPetName() { return petName; }
    public long getVoteCount() { return voteCount; }
}
