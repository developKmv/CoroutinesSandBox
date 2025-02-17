data class FlightStatus(
    val flightNumber: String,
    val passengerName: String,
    val passengerLoyaltyTier: LoyaltyTier,
    val originAirport: String,
    val destinationAirport: String,
    val status: String,
    val departureTimeInMinutes: Int
) {
    val isFlightCanceled: Boolean
        get() = status.equals("Canceled", ignoreCase = true)
    val hasBoardingStarted: Boolean
        get() = departureTimeInMinutes in 15..60
    val isBoardingOver: Boolean
        get() = departureTimeInMinutes < 15
    val isEligibleToBoard: Boolean
        get() = departureTimeInMinutes in 15..passengerLoyaltyTier.boardingWindowStart
    val boardingStatus: BoardingState
        get() = when {
            isFlightCanceled -> BoardingState.FlightCanceled
            isBoardingOver -> BoardingState.BoardingEnded
            isEligibleToBoard -> BoardingState.Boarding
            hasBoardingStarted -> BoardingState.WaitingToBoard
            else -> BoardingState.BoardingNotStarted
        }

    companion object {
        fun parse(
            flightResponse: String,
            loyaltyResponse: String,
            passengerName: String
        ): FlightStatus {
            val (flightNumber, originAirport, destinationAirport, status,
                departureTimeInMinutes) = flightResponse.split(",")
            val (loyaltyTierName, milesFlown, milesToNextTier) =
                loyaltyResponse.split(",")
            return FlightStatus(
                flightNumber = flightNumber,
                passengerName = passengerName,
                passengerLoyaltyTier = LoyaltyTier.values().first { it.tierName == loyaltyTierName },
                originAirport = originAirport,
                destinationAirport = destinationAirport,
                status = status,
                departureTimeInMinutes = departureTimeInMinutes.toInt()
            )
        }
    }
}