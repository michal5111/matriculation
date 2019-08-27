export interface Application {
    id: number,
            user: number,
            payment: string,
            score: string,
            position: string,
            qualified: string,
            admitted: string,
            comment: string,
            turn: {
                programme: string,
                registration: string,
                date_from: Date,
                date_to: Date
            },
            foreigner_data: {
                base_of_stay: string,
                source_of_financing: string,
                basis_of_admission: string
            }
}
