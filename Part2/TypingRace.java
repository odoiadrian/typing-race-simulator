import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author Adrian Odoi  `

* @version 29/04/2026
 */
public class TypingRace{
    private final int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    //Typist initial accuracies
    private double initAcc1;
    private double initAcc2;
    private double initAcc3;

    public static void main(String args[]) throws IOException{
        
        TypingRace race = new TypingRace(40);
        race.addTypist(new Typist('A', "Adrian", 0.6), 1);
        race.addTypist(new Typist('B', "Ben", 0.5), 2);
        race.addTypist(new Typist('C', "Charlie", 0.4), 3);
        race.startRace();
    }

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength){
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber){
        
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
        else
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }


    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     *
     * Note from Ty: "I didn't bother printing the winner at the end,
     * you can probably figure that out yourself."
     */
    public void startRace() throws IOException{

        if (seat1Typist == null || seat2Typist == null || seat3Typist == null){
            return;
        }

        double initialAccuracy1 = seat1Typist.getAccuracy();
        double initialAccuracy2 = seat2Typist.getAccuracy();
        double initialAccuracy3 = seat3Typist.getAccuracy();
        double winnerAccuracy = 0;
        boolean finished = false;
        Typist winner = null;

        // Reset all typists to the start of the passage
        // (Ty was in a hurry here)
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        while (!finished)
        {
            // Advance each typist by one turn
            advanceTypist(seat1Typist);
            advanceTypist(seat2Typist);
            advanceTypist(seat3Typist);

            // Print the current state of the race
            printRace();
            getRaceState();

            // Check if any typist has finished the passage. If so, set them as the winner and end the race
            if (raceFinishedBy(seat1Typist)){
                winner = seat1Typist;
                winnerAccuracy = initialAccuracy1;
                finished = true;
            }
            else if (raceFinishedBy(seat2Typist)){
                winner = seat2Typist;
                winnerAccuracy = initialAccuracy2;
                finished = true;
            }
            else if (raceFinishedBy(seat3Typist)){
                winner = seat2Typist;
                winnerAccuracy = initialAccuracy3;
                finished = true;
            }

            // Check if any typist has finished the passage
            if ( raceFinishedBy(seat1Typist) || raceFinishedBy(seat2Typist) || raceFinishedBy(seat3Typist) )
            {
                finished = true;
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // TODO (Task 2a): Print the winner's name here
        if (winner != null){
            /*Increases accuracy of the winner.
            Longer races give bigger boosts
            More accurate typists get smaller boosts
            Equation ensures that accuracy never goes above 1.0
            */
           winner.setAccuracy(winner.getAccuracy() + (0.08 * this.passageLength / 1000)*(1 - winner.getAccuracy()));

            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.print("Final accuracy: " + winner.getAccuracy());
            if (winnerAccuracy <= winner.getAccuracy()){
                System.out.println(" (improved from " + winnerAccuracy + ")");
            }
            else{
                System.out.println(" (regressed from " + winnerAccuracy + ")");
            }
            
        }
    }

    public Typist getWinner() throws IOException{
        Typist winner = null; 
        if (raceFinishedBy(seat1Typist)){
            winner = seat1Typist;
        }
        else if (raceFinishedBy(seat2Typist)){
            winner = seat2Typist;
        }
        else if (raceFinishedBy(seat3Typist)){
            winner = seat3Typist;
        }
        return winner;
    }


    

    public void stepRace() {
        advanceTypist(seat1Typist);
        advanceTypist(seat2Typist);
        advanceTypist(seat3Typist);
    }

    public boolean isFinished() throws IOException {
        return (raceFinishedBy(seat1Typist) || raceFinishedBy(seat2Typist) || raceFinishedBy(seat3Typist));
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist){
        if (theTypist == null){
            return;
        }
        if (theTypist.isBurntOut())
        {
            theTypist.setMistyped(false);
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.setMistyped(false);
            theTypist.typeCharacter();
            theTypist.setAccuracyPercentage(theTypist.getAccuracyPercentage() + 1);
            theTypist.incrementCharactersTyped();
            return;
        }

        // Mistype check — the probability should reflect the typist's accuracy
        if (Math.random() < (1 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.setMistyped(true);
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setAccuracyPercentage(theTypist.getAccuracyPercentage() - 1);
            return;
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.setMistyped(false);
            theTypist.burnOut(BURNOUT_DURATION);
            /* Slightly reduces accuracy for typists that burnout
            Longer races cause higher accuracy drops
            Higher accuracy typists get higher accuracy drops
            Ensures the accuracy never goes below 0
             */
            theTypist.setAccuracy(theTypist.getAccuracy() - (theTypist.getAccuracy() * 0.05 * this.passageLength / 1000));
            return;
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist) throws IOException
    {
        // Ty was confident this condition was correct
        if (theTypist.getProgress() == passageLength)
        {
            seat1Typist.setAccuracyPercentage(100 * seat1Typist.getAccuracyPercentage()/seat1Typist.getCharactersTyped());
            FileWriter writer1 = new FileWriter(seat1Typist.getName() + ".txt");
            writer1.write("Accuracy Percentage: " + seat1Typist.getAccuracyPercentage() + "%");
            writer1.close();

            seat2Typist.setAccuracyPercentage(100 * seat2Typist.getAccuracyPercentage()/seat2Typist.getCharactersTyped());
            FileWriter writer2 = new FileWriter(seat2Typist.getName() + ".txt");
            writer2.write("Accuracy Percentage: " + seat2Typist.getAccuracyPercentage() + "%");
            writer2.close();

            seat3Typist.setAccuracyPercentage(100 *seat3Typist.getAccuracyPercentage()/seat3Typist.getCharactersTyped());FileWriter writer = new FileWriter(seat1Typist.getName() + ".txt");
            FileWriter writer3 = new FileWriter(seat3Typist.getName() + ".txt");
            writer3.write("Accuracy Percentage: " + seat3Typist.getAccuracyPercentage() + "%");
            writer3.close();

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist);
        System.out.println();

        printSeat(seat2Typist);
        System.out.println();

        printSeat(seat3Typist);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          ⌨           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [~]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement — perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();
        
        if (spacesAfter < 0){
            spacesAfter = 0;
        }

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }
        else if (theTypist.hasMistyped()){
            System.out.print('<');
            spacesAfter--;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else if (theTypist.hasMistyped()){
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " MISTYPED");
        }
        else{
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    public String getRaceState() throws IOException{
        StringBuilder output = new StringBuilder();

        output.append("TYPING RACE - passage length: ").append(this.passageLength).append(" chars\n\n");
        for (int i = 0; i < (this.passageLength/2); i++){
            output.append("=");
        }
        output.append("\n\n");

        output.append(getLane(seat1Typist)).append("\n\n");
        output.append(getLane(seat2Typist)).append("\n\n");
        output.append(getLane(seat3Typist)).append("\n\n");

        for (int i = 0; i < (this.passageLength)/2; i++){
            output.append("=");
        }
        output.append("\n\n");
        output.append(endMessage());

        return output.toString();
    }

    private String getLane(Typist theTypist){
        if (theTypist == null){
            return "| Empty Lane |";
        }
        StringBuilder lane = new StringBuilder();

        int spacesBefore = theTypist.getProgress();
        int spacesAfter = passageLength - theTypist.getProgress();

        lane.append("|");

        for (int i = 0; i < spacesBefore; i++) {
            lane.append(" ");
        }

        lane.append(theTypist.getSymbol());

        if (theTypist.isBurntOut()) {
            lane.append("~");
            spacesAfter--;
        } else if (theTypist.hasMistyped()) {
            lane.append("<");
            spacesAfter--;
        }

        for (int i = 0; i < (spacesAfter); i++) {
            lane.append(" ");
        }

        lane.append("| ");

                // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            lane.append(theTypist.getName()).append(" (Accuracy: ").append(theTypist.getAccuracy()).append(") BURNT OUT (").append(theTypist.getBurnoutTurnsRemaining()).append(" turns)");
        }
        else if (theTypist.hasMistyped()){
            lane.append(theTypist.getName()).append(" (Accuracy: ").append(theTypist.getAccuracy()).append(") MISTYPED");
        }
        else{
            lane.append(theTypist.getName());
            lane.append(" (Accuracy: ").append(theTypist.getAccuracy()).append(")");
        }




        return lane.toString();

    }

    public String endMessage () throws IOException{
        String finalMessage = "";
        if (this.isFinished()){
            Typist winner = this.getWinner();
            /*Increases accuracy of the winner.
            Longer races give bigger boosts
            More accurate typists get smaller boosts
            Equation ensures that accuracy never goes above 1.0
            */
           winner.setAccuracy(winner.getAccuracy() + (0.08 * this.passageLength / 1000)*(1 - winner.getAccuracy()));

            finalMessage += ("And the winner is... " + winner.getName() + "!\n");
            finalMessage += ("Final accuracy: " + winner.getAccuracy());
            if (winner.getInitialAccuracy() <= winner.getAccuracy()){
                finalMessage += (" (improved from " + winner.getInitialAccuracy() + ")");
            }
            else{
                finalMessage += (" (regressed from " + winner.getInitialAccuracy() + ")");
            }

        }
        return finalMessage;

    }

    public double getPassageLength() {
        return this.passageLength;
    }

}
