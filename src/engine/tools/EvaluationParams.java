package engine.tools;

public class EvaluationParams {
    public static final int PAWN_VALUE_MID = 126, KNIGHT_VALUE_MID = 781, BISHOP_VALUE_MID = 825, ROOK_VALUE_MID = 1276, QUEEN_VALUE_MID = 2538;
    public static final int PAWN_VALUE_END = 208, KNIGHT_VALUE_END = 854, BISHOP_VALUE_END = 915, ROOK_VALUE_END = 1380, QUEEN_VALUE_END = 2682;
    private static final int QUEEN_VALUE = 930, KING_VALUE = 60000;
    private static final int C_B = KING_VALUE + 10 * QUEEN_VALUE;

    public static final int[] PAWN_PST_MID = new int[]{
           0,    0,    0,    0,    0,    0,    0,    0,
           2,    4,   11,   18,   16,   21,    9,   -3,
          -9,  -15,   11,   15,   31,   23,    6,  -20,
          -3,  -20,    8,   19,   39,   17,    2,   -5,
          11,   -4,  -11,    2,   11,    0,  -12,    5,
           3,  -11,   -6,   22,   -8,   -5,  -14,  -11,
          -7,    6,   -2,  -11,    4,  -14,   10,   -9,
           0,    0,    0,    0,    0,    0,    0,    0
    };

    public static final int[] PAWN_PST_END = new int[]{
           0,    0,    0,    0,    0,    0,    0,    0,
          -8,   -6,    9,    5,   16,    6,   -6,  -18,
          -9,   -7,  -10,    5,    2,    3,   -8,   -5,
           7,    1,   -8,   -2,  -14,  -13,  -11,   -6,
          12,    6,    2,   -6,   -5,   -4,   14,    9,
          27,   18,   19,   29,   30,    9,    8,   14,
          -1,  -14,   13,   22,   24,   17,    7,    7,
           0,    0,    0,    0,    0,    0,    0,    0
    };

    public static final int[] KNIGHT_PST_MID = new int[]{
        -175,  -92,  -74,  -73,  -73,  -74,  -92, -175,
         -77,  -41,  -27,  -15,  -15,  -27,  -41,  -77,
         -61,  -17,    6,   12,   12,    6,  -17,  -61,
         -35,    8,   40,   49,   49,   40,    8,  -35,
         -34,   13,   44,   51,   51,   44,   13,  -34,
          -9,   22,   58,   53,   53,   58,   22,   -9,
         -67,  -27,    4,   37,   37,    4,  -27,  -67,
        -201,  -83,  -56,  -26,  -26,  -56,  -83, -201
    };

    public static final int[] KNIGHT_PST_END = new int[]{
         -96,  -65,  -49,  -21,  -21,  -49,  -65,  -96,
         -67,  -54,  -18,    8,    8,  -18,  -54,  -67,
         -40,  -27,   -8,   29,   29,   -8,  -27,  -40,
         -35,   -2,   13,   28,   28,   13,   -2,  -35,
         -45,  -16,    9,   39,   39,    9,  -16,  -45,
         -51,  -44,  -16,   17,   17,  -16,  -44,  -51,
         -69,  -50,  -51,   12,   12,  -51,  -50,  -69,
        -100,  -88,  -56,  -17,  -17,  -56,  -88, -100
    };

    public static final int[] BISHOP_PST_MID = new int[]{
         -37,   -4,   -6,  -16,  -16,   -6,   -4,  -37,
         -11,    6,   13,    3,    3,   13,    6,  -11,
          -5,   15,   -4,   12,   12,   -4,   15,   -5,
          -4,    8,   18,   27,   27,   18,    8,   -4,
          -8,   20,   15,   22,   22,   15,   20,   -8,
         -11,    4,    1,    8,    8,    1,    4,  -11,
         -12,  -10,    4,    0,    0,    4,  -10,  -12,
         -34,    1,  -10,  -16,  -16,  -10,    1,  -34
    };

    public static final int[] BISHOP_PST_END = new int[]{
         -40,  -21,  -26,   -8,   -8,  -26,  -21,  -40,
         -26,   -9,  -12,    1,    1,  -12,   -9,  -26,
         -11,   -1,   -1,    7,    7,   -1,   -1,  -11,
         -14,   -4,    0,   12,   12,    0,   -4,  -14,
         -12,   -1,  -10,   11,   11,  -10,   -1,  -12,
         -21,    4,    3,    4,    4,    3,    4,  -21,
         -22,  -14,   -1,    1,    1,   -1,  -14,  -22,
         -32,  -29,  -26,  -17,  -17,  -26,  -29,  -32
    };

    public static final int[] ROOK_PST_MID = new int[]{
         -31,  -20,  -14,   -5,   -5,  -14,  -20,  -31,
         -21,  -13,   -8,    6,    6,   -8,  -13,  -21,
         -25,  -11,   -1,    3,    3,   -1,  -11,  -25,
         -13,   -5,   -4,   -6,   -6,   -4,   -5,  -13,
         -27,  -15,   -4,    3,    3,   -4,  -15,  -27,
         -22,   -2,    6,   12,   12,    6,   -2,  -22,
          -2,   12,   16,   18,   18,   16,   12,   -2,
         -17,  -19,   -1,    9,    9,   -1,  -19,  -17
    };

    public static final int[] ROOK_PST_END = new int[]{
          -9,  -13,  -10,   -9,   -9,  -10,  -13,   -9,
         -12,   -9,   -1,   -2,   -2,   -1,   -9,  -12,
           6,   -8,   -2,   -6,   -6,   -2,   -8,    6,
          -6,    1,   -9,    7,    7,   -9,    1,   -6,
          -5,    8,    7,   -6,   -6,    7,    8,   -5,
           6,    1,   -7,   10,   10,   -7,    1,    6,
           4,    5,   20,   -5,   -5,   20,    5,    4,
          18,    0,   19,   13,   13,   19,    0,   18
    };

    public static final int[] QUEEN_PST_MID = new int[]{
           3,   -5,   -5,    4,    4,   -5,   -5,    3,
          -3,    5,    8,   12,   12,    8,    5,   -3,
          -3,    6,   13,    7,    7,   13,    6,   -3,
           4,    5,    9,    8,    8,    9,    5,    4,
           0,   14,   12,    5,    5,   12,   14,    0,
          -4,   10,    6,    8,    8,    6,   10,   -4,
          -5,    6,   10,    8,    8,   10,    6,   -5,
          -2,   -2,    1,   -2,   -2,    1,   -2,   -2
    };

    public static final int[] QUEEN_PST_END = new int[]{
         -69,  -57,  -47,  -26,  -26,  -47,  -57,  -69,
         -54,  -31,  -22,   -4,   -4,  -22,  -31,  -54,
         -39,  -18,   -9,    3,    3,   -9,  -18,  -39,
         -23,   -3,   13,   24,   24,   13,   -3,  -23,
         -29,   -6,    9,   21,   21,    9,   -6,  -29,
         -38,  -18,  -11,    1,    1,  -11,  -18,  -38,
         -50,  -27,  -24,   -8,   -8,  -24,  -27,  -50,
         -74,  -52,  -43,  -34,  -34,  -43,  -52,  -74
    };

    public static final int[] KING_PST_MID = new int[]{
         271,  327,  271,  189,  189,  271,  327,  271,
         278,  303,  234,  179,  179,  234,  303,  278,
         195,  258,  169,  120,  120,  169,  258,  195,
         164,  190,  138,  C_B,  C_B,  138,  190,  164,
         154,  179,  105,  C_B,  C_B,  105,  179,  154,
         123,  145,   81,   81,   81,   81,  145,  123,
          88,  120,   65,   33,   33,   65,  120,   88,
          59,   89,   45,   -1,   -1,   45,   89,   59
    };

    public static final int[] KING_PST_END = new int[]{
           1,   45,   85,   76,   76,   85,   45,    1,
          53,  100,  133,  135,  135,  133,  100,   53,
          88,  130,  569,  575,  575,  569,  130,   88,
         103,  156,  572,  C_B,  C_B,  572,  156,  103,
          96,  166,  599,  C_B,  C_B,  599,  166,   96,
          92,  172,  584,  591,  591,  584,  172,   92,
          47,  121,  316,  331,  331,  316,  121,   47,
          11,   59,   73,   78,   78,   73,   59,   11
    };

    public static final int[] SINGLE_MOVES_TO_CENTER_COUNT = new int[]{
        3, 3, 3, 3, 3, 3, 3, 3,
        3, 2, 2, 2, 2, 2, 2, 3,
        3, 2, 1, 1, 1, 1, 2, 3,
        3, 2, 1, 0, 0, 1, 2, 3,
        3, 2, 1, 0, 0, 1, 2, 3,
        3, 2, 1, 1, 1, 1, 2, 3,
        3, 2, 2, 2, 2, 2, 2, 3,
        3, 3, 3, 3, 3, 3, 3, 3
    };

    public static final int[] KNIGHT_MOBILITY_BONUS_MID = new int[]{-62, -53, -12, -3, 3, 12, 21, 28, 37};
    public static final int[] KNIGHT_MOBILITY_BONUS_END = new int[]{-79, -57, -31, -17, 7, 13, 16, 21, 26};
    public static final int[] BISHOP_MOBILITY_BONUS_MID = new int[]{-47, -20, 14, 29, 39, 53, 53, 60, 62, 69, 78, 83, 91, 96};
    public static final int[] BISHOP_MOBILITY_BONUS_END = new int[]{-59, -25, -8, 12, 21, 40, 56, 58, 65, 72, 78, 87, 88, 98};
    public static final int[] ROOK_MOBILITY_BONUS_MID = new int[]{-60, -24, 0, 3, 4, 14, 20, 30, 41, 41, 41, 45, 57, 58, 67};
    public static final int[] ROOK_MOBILITY_BONUS_END = new int[]{-82, -15, 17, 43, 72, 100, 102, 122, 133, 139, 153, 160, 165, 170, 175};
    public static final int[] QUEEN_MOBILITY_BONUS_MID = new int[]{-29, -16, -8, -8, 18, 25, 23, 37, 41, 54, 65, 68, 69, 70, 70, 70, 71, 72, 74, 76, 90, 104, 105, 106, 112, 114, 114, 119};
    public static final int[] QUEEN_MOBILITY_BONUS_END = new int[]{-49, -29, -8, 17, 39, 54, 59, 73, 76, 95, 95, 101, 124, 128, 132, 133, 136, 140, 147, 149, 153, 169, 171, 171, 178, 185, 187, 221};

    public static final int ISOLATED_PAWN_PENALTY_MID = -1;
    public static final int ISOLATED_PAWN_PENALTY_END = -20;
    public static final int DOUBLED_PAWN_PENALTY_MID = -11;
    public static final int DOUBLED_PAWN_PENALTY_END = -51;
    public static final int BLOCKED_PAWN_PENALTY_MID = -6;
    public static final int BLOCKED_PAWN_PENALTY_END = -1;

    public static final int KNIGHT_OUTPOST_BONUS_MID = 54;
    public static final int KNIGHT_OUTPOST_BONUS_END = 34;
    public static final int BISHOP_OUTPOST_BONUS_MID = 31;
    public static final int BISHOP_OUTPOST_BONUS_END = 25;

    public static final int BISHOP_PAIR_BONUS_MID = 120;
    public static final int BISHOP_PAIR_BONUS_END = 175;

    public static final int KING_MOBILITY_BONUS_MID = 8;
    public static final int KING_MOBILITY_BONUS_END = 45;
    public static final int MOVES_TO_CENTER_PENALTY_MID = 1;
    public static final int MOVES_TO_CENTER_PENALTY_END = -100;

    public static final int ROOK_ON_CLOSED_FILE_BONUS_MID = 10;
    public static final int ROOK_ON_CLOSED_FILE_BONUS_END = 5;
    public static final int ROOK_ON_SEMI_OPEN_FILE_BONUS_MID = 18;
    public static final int ROOK_ON_SEMI_OPEN_FILE_BONUS_END = 8;
    public static final int ROOK_ON_OPEN_FILE_BONUS_MID = 49;
    public static final int ROOK_ON_OPEN_FILE_BONUS_END = 26;

    public static final int[] ATTACKED_BY_KNIGHT_OR_BISHOP_MID = new int[]{6, 64, 82, 103, 81};
    public static final int[] ATTACKED_BY_KNIGHT_OR_BISHOP_END = new int[]{37, 50, 57, 130, 163};
    public static final int[] ATTACKED_BY_ROOK_MID = new int[]{3, 36, 44, 0, 60};
    public static final int[] ATTACKED_BY_ROOK_END = new int[]{44, 71, 59, 39, 39};

    public static int getMirroredIndex(int index){
        int mirroredRank = 7 - (index / 8);
        int file = index % 8;
        return mirroredRank * 8 + file;
    }

}

/*
Durch genetischen Algorithmus optimierte Parameter:

public static final int PAWN_VALUE_MID = 114;
public static final int KNIGHT_VALUE_MID = 859;
public static final int BISHOP_VALUE_MID = 825;
public static final int ROOK_VALUE_MID = 1403;
public static final int QUEEN_VALUE_MID = 2791;
public static final int PAWN_VALUE_END = 188;
public static final int KNIGHT_VALUE_END = 845;
public static final int BISHOP_VALUE_END = 1006;
public static final int ROOK_VALUE_END = 1242;
public static final int QUEEN_VALUE_END = 2950;
public static final int[] PAWN_PST_MID = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 13, 18, 16, 23, 9, -3, -9, -14, 12, 15, 31, 23, 6, -22, -3, -22, 8, 19, 42, 16, 2, -5, 11, -4, -11, 2, 10, 0, -13, 5, 3, -11, -6, 20, -8, -5, -16, -11, -7, 6, -2, -11, 4, -15, 11, -9, 0, 0, 0, 0, 0, 0, 0, 0};
public static final int[] PAWN_PST_END = new int[]{0, 0, 0, 0, 0, 0, 0, 0, -8, -6, 9, 5, 16, 6, -6, -18, -9, -7, -9, 5, 2, 3, -8, -5, 7, 1, -8, -2, -14, -12, -11, -6, 12, 6, 2, -6, -5, -4, 14, 9, 27, 17, 18, 27, 27, 9, 8, 16, -1, -14, 12, 20, 22, 16, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0};
public static final int[] KNIGHT_PST_MID = new int[]{-192, -92, -66, -80, -73, -81, -92, -173, -84, -41, -25, -16, -13, -29, -31, -84, -55, -17, 6, 11, 11, 6, -15, -67, -35, 8, 36, 48, 45, 33, 8, -35, -31, 13, 44, 61, 46, 48, 13, -34, -9, 22, 58, 53, 48, 53, 20, -9, -67, -25, 4, 34, 37, 4, -27, -61, -201, -83, -61, -26, -24, -67, -91, -181};
public static final int[] KNIGHT_PST_END = new int[]{-87, -59, -49, -21, -21, -49, -65, -105, -67, -54, -18, 8, 8, -18, -59, -61, -44, -25, -8, 27, 31, -8, -25, -44, -29, -2, 13, 30, 28, 13, -2, -35, -53, -17, 9, 36, 39, 9, -16, -49, -51, -40, -16, 16, 16, -15, -40, -46, -69, -50, -56, 13, 12, -51, -45, -69, -99, -88, -61, -16, -16, -56, -96, -100};
public static final int[] BISHOP_PST_MID = new int[]{-40, -4, -6, -15, -16, -6, -4, -31, -11, 6, 13, 3, 3, 14, 6, -10, -5, 15, -4, 12, 13, -4, 15, -5, -4, 8, 18, 25, 29, 19, 8, -4, -8, 22, 15, 24, 22, 15, 18, -8, -10, 4, 1, 8, 8, 1, 4, -11, -12, -10, 4, 0, 0, 4, -9, -11, -37, 1, -10, -14, -17, -10, 1, -34};
public static final int[] BISHOP_PST_END = new int[]{-40, -25, -24, -8, -8, -26, -21, -44, -30, -9, -11, 1, 1, -12, -9, -28, -10, -1, -1, 7, 7, -1, -1, -11, -14, -4, 0, 11, 13, 0, -4, -15, -12, -1, -10, 12, 13, -10, -1, -13, -19, 4, 3, 4, 4, 3, 4, -21, -20, -14, -1, 1, 1, -1, -14, -24, -35, -34, -28, -18, -18, -28, -29, -32};
public static final int[] ROOK_PST_MID = new int[]{-28, -18, -16, -5, -5, -14, -24, -34, -19, -13, -8, 6, 6, -8, -13, -20, -25, -11, -1, 3, 3, -1, -11, -27, -12, -5, -4, -6, -6, -4, -5, -14, -27, -16, -4, 3, 3, -4, -15, -26, -20, -2, 6, 13, 12, 6, -2, -22, -2, 13, 16, 18, 18, 17, 12, -2, -17, -20, -1, 9, 9, -1, -20, -17};
public static final int[] ROOK_PST_END = new int[]{-9, -12, -12, -9, -9, -10, -14, -9, -13, -9, -1, -2, -2, -1, -9, -12, 6, -8, -2, -6, -6, -2, -8, 6, -6, 1, -9, 7, 7, -9, 1, -6, -5, 8, 7, -6, -6, 7, 8, -5, 6, 1, -7, 11, 9, -7, 1, 6, 4, 5, 20, -5, -5, 18, 5, 4, 19, 0, 18, 15, 14, 18, 0, 18};
public static final int[] QUEEN_PST_MID = new int[]{3, -5, -5, 4, 4, -5, -5, 3, -3, 5, 8, 13, 12, 8, 5, -3, -3, 6, 12, 7, 7, 13, 6, -3, 4, 5, 9, 8, 8, 9, 5, 4, 0, 13, 13, 5, 5, 11, 13, 0, -4, 9, 6, 8, 8, 6, 11, -4, -5, 6, 10, 8, 8, 11, 6, -5, -2, -2, 1, -2, -2, 1, -2, -2};
public static final int[] QUEEN_PST_END = new int[]{-69, -52, -43, -26, -26, -47, -57, -69, -54, -28, -24, -4, -4, -24, -31, -54, -42, -17, -9, 3, 3, -9, -18, -36, -23, -3, 13, 24, 22, 14, -3, -25, -29, -6, 9, 21, 19, 9, -6, -31, -35, -18, -11, 1, 1, -11, -18, -35, -45, -25, -24, -8, -8, -26, -25, -50, -74, -43, -43, -31, -31, -42, -57, -73};
public static final int[] KING_PST_MID = new int[]{298, 359, 244, 189, 171, 220, 295, 268, 251, 273, 257, 162, 162, 257, 303, 251, 195, 256, 153, 145, 120, 185, 258, 159, 134, 171, 151, 68607, 69300, 166, 190, 180, 154, 162, 105, 83853, 68607, 115, 179, 169, 123, 158, 81, 73, 81, 89, 131, 111, 87, 108, 59, 30, 30, 65, 108, 88, 54, 106, 45, -1, -1, 49, 89, 84};
public static final int[] KING_PST_END = new int[]{1, 49, 85, 83, 83, 77, 41, 1, 48, 121, 132, 122, 135, 120, 100, 63, 80, 143, 513, 518, 518, 462, 129, 88, 103, 156, 515, 76230, 62370, 515, 156, 103, 96, 166, 599, 69300, 69300, 658, 166, 96, 92, 189, 526, 591, 585, 636, 189, 83, 56, 133, 316, 331, 328, 347, 146, 47, 12, 69, 73, 71, 71, 66, 70, 10};
public static final int[] KNIGHT_MOBILITY_BONUS_MID = new int[]{-56, -58, -11, -3, 3, 10, 19, 30, 37};
public static final int[] KNIGHT_MOBILITY_BONUS_END = new int[]{-79, -62, -31, -15, 7, 12, 15, 19, 28};
public static final int[] BISHOP_MOBILITY_BONUS_MID = new int[]{-43, -20, 14, 29, 42, 53, 58, 66, 74, 52, 71, 82, 82, 105};
public static final int[] BISHOP_MOBILITY_BONUS_END = new int[]{-54, -25, -8, 13, 18, 48, 56, 63, 71, 65, 71, 87, 96, 89};
public static final int[] ROOK_MOBILITY_BONUS_MID = new int[]{-66, -26, 0, 3, 4, 15, 20, 30, 37, 45, 41, 45, 57, 58, 67};
public static final int[] ROOK_MOBILITY_BONUS_END = new int[]{-82, -14, 19, 39, 65, 100, 92, 122, 133, 139, 168, 160, 165, 187, 158};
public static final int[] QUEEN_MOBILITY_BONUS_MID = new int[]{-31, -16, -8, -8, 18, 25, 23, 36, 41, 54, 59, 89, 69, 70, 77, 70, 71, 72, 81, 83, 81, 103, 115, 96, 101, 114, 103, 130};
public static final int[] QUEEN_MOBILITY_BONUS_END = new int[]{-53, -29, -8, 16, 46, 64, 59, 73, 63, 95, 86, 100, 124, 128, 118, 144, 136, 154, 147, 149, 153, 185, 169, 154, 145, 185, 185, 243};
public static final int ISOLATED_PAWN_PENALTY_MID = -1;
public static final int ISOLATED_PAWN_PENALTY_END = -20;
public static final int DOUBLED_PAWN_PENALTY_MID = -11;
public static final int DOUBLED_PAWN_PENALTY_END = -51;
public static final int BLOCKED_PAWN_PENALTY_MID = -6;
public static final int BLOCKED_PAWN_PENALTY_END = -1;
public static final int KNIGHT_OUTPOST_BONUS_MID = 49;
public static final int KNIGHT_OUTPOST_BONUS_END = 34;
public static final int BISHOP_OUTPOST_BONUS_MID = 31;
public static final int BISHOP_OUTPOST_BONUS_END = 27;
public static final int BISHOP_PAIR_BONUS_MID = 120;
public static final int BISHOP_PAIR_BONUS_END = 175;
public static final int KING_MOBILITY_BONUS_MID = 8;
public static final int KING_MOBILITY_BONUS_END = 45;
public static final int MOVES_TO_CENTER_PENALTY_MID = 1;
public static final int MOVES_TO_CENTER_PENALTY_END = -90;
public static final int ROOK_ON_CLOSED_FILE_BONUS_MID = 11;
public static final int ROOK_ON_CLOSED_FILE_BONUS_END = 5;
public static final int ROOK_ON_SEMI_OPEN_FILE_BONUS_MID = 20;
public static final int ROOK_ON_SEMI_OPEN_FILE_BONUS_END = 8;
public static final int ROOK_ON_OPEN_FILE_BONUS_MID = 49;
public static final int ROOK_ON_OPEN_FILE_BONUS_END = 24;
public static final int[] ATTACKED_BY_KNIGHT_OR_BISHOP_MID = new int[]{6, 64, 74, 103, 66};
public static final int[] ATTACKED_BY_KNIGHT_OR_BISHOP_END = new int[]{37, 45, 62, 143, 179};
public static final int[] ATTACKED_BY_ROOK_MID = new int[]{3, 39, 48, 0, 54};
public static final int[] ATTACKED_BY_ROOK_END = new int[]{44, 71, 59, 36, 36};

Zum Laden in eine GeneticNegamax-Instanz:
new Chromosome(new EvolutionData(), new Integer[]{114, 859, 825, 1403, 2791, 188, 845, 1006, 1242, 2950, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 13, 18, 16, 23, 9, -3, -9, -14, 12, 15, 31, 23, 6, -22, -3, -22, 8, 19, 42, 16, 2, -5, 11, -4, -11, 2, 10, 0, -13, 5, 3, -11, -6, 20, -8, -5, -16, -11, -7, 6, -2, -11, 4, -15, 11, -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8, -6, 9, 5, 16, 6, -6, -18, -9, -7, -9, 5, 2, 3, -8, -5, 7, 1, -8, -2, -14, -12, -11, -6, 12, 6, 2, -6, -5, -4, 14, 9, 27, 17, 18, 27, 27, 9, 8, 16, -1, -14, 12, 20, 22, 16, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, -192, -92, -66, -80, -73, -81, -92, -173, -84, -41, -25, -16, -13, -29, -31, -84, -55, -17, 6, 11, 11, 6, -15, -67, -35, 8, 36, 48, 45, 33, 8, -35, -31, 13, 44, 61, 46, 48, 13, -34, -9, 22, 58, 53, 48, 53, 20, -9, -67, -25, 4, 34, 37, 4, -27, -61, -201, -83, -61, -26, -24, -67, -91, -181, -87, -59, -49, -21, -21, -49, -65, -105, -67, -54, -18, 8, 8, -18, -59, -61, -44, -25, -8, 27, 31, -8, -25, -44, -29, -2, 13, 30, 28, 13, -2, -35, -53, -17, 9, 36, 39, 9, -16, -49, -51, -40, -16, 16, 16, -15, -40, -46, -69, -50, -56, 13, 12, -51, -45, -69, -99, -88, -61, -16, -16, -56, -96, -100, -40, -4, -6, -15, -16, -6, -4, -31, -11, 6, 13, 3, 3, 14, 6, -10, -5, 15, -4, 12, 13, -4, 15, -5, -4, 8, 18, 25, 29, 19, 8, -4, -8, 22, 15, 24, 22, 15, 18, -8, -10, 4, 1, 8, 8, 1, 4, -11, -12, -10, 4, 0, 0, 4, -9, -11, -37, 1, -10, -14, -17, -10, 1, -34, -40, -25, -24, -8, -8, -26, -21, -44, -30, -9, -11, 1, 1, -12, -9, -28, -10, -1, -1, 7, 7, -1, -1, -11, -14, -4, 0, 11, 13, 0, -4, -15, -12, -1, -10, 12, 13, -10, -1, -13, -19, 4, 3, 4, 4, 3, 4, -21, -20, -14, -1, 1, 1, -1, -14, -24, -35, -34, -28, -18, -18, -28, -29, -32, -28, -18, -16, -5, -5, -14, -24, -34, -19, -13, -8, 6, 6, -8, -13, -20, -25, -11, -1, 3, 3, -1, -11, -27, -12, -5, -4, -6, -6, -4, -5, -14, -27, -16, -4, 3, 3, -4, -15, -26, -20, -2, 6, 13, 12, 6, -2, -22, -2, 13, 16, 18, 18, 17, 12, -2, -17, -20, -1, 9, 9, -1, -20, -17, -9, -12, -12, -9, -9, -10, -14, -9, -13, -9, -1, -2, -2, -1, -9, -12, 6, -8, -2, -6, -6, -2, -8, 6, -6, 1, -9, 7, 7, -9, 1, -6, -5, 8, 7, -6, -6, 7, 8, -5, 6, 1, -7, 11, 9, -7, 1, 6, 4, 5, 20, -5, -5, 18, 5, 4, 19, 0, 18, 15, 14, 18, 0, 18, 3, -5, -5, 4, 4, -5, -5, 3, -3, 5, 8, 13, 12, 8, 5, -3, -3, 6, 12, 7, 7, 13, 6, -3, 4, 5, 9, 8, 8, 9, 5, 4, 0, 13, 13, 5, 5, 11, 13, 0, -4, 9, 6, 8, 8, 6, 11, -4, -5, 6, 10, 8, 8, 11, 6, -5, -2, -2, 1, -2, -2, 1, -2, -2, -69, -52, -43, -26, -26, -47, -57, -69, -54, -28, -24, -4, -4, -24, -31, -54, -42, -17, -9, 3, 3, -9, -18, -36, -23, -3, 13, 24, 22, 14, -3, -25, -29, -6, 9, 21, 19, 9, -6, -31, -35, -18, -11, 1, 1, -11, -18, -35, -45, -25, -24, -8, -8, -26, -25, -50, -74, -43, -43, -31, -31, -42, -57, -73, 298, 359, 244, 189, 171, 220, 295, 268, 251, 273, 257, 162, 162, 257, 303, 251, 195, 256, 153, 145, 120, 185, 258, 159, 134, 171, 151, 68607, 69300, 166, 190, 180, 154, 162, 105, 83853, 68607, 115, 179, 169, 123, 158, 81, 73, 81, 89, 131, 111, 87, 108, 59, 30, 30, 65, 108, 88, 54, 106, 45, -1, -1, 49, 89, 84, 1, 49, 85, 83, 83, 77, 41, 1, 48, 121, 132, 122, 135, 120, 100, 63, 80, 143, 513, 518, 518, 462, 129, 88, 103, 156, 515, 76230, 62370, 515, 156, 103, 96, 166, 599, 69300, 69300, 658, 166, 96, 92, 189, 526, 591, 585, 636, 189, 83, 56, 133, 316, 331, 328, 347, 146, 47, 12, 69, 73, 71, 71, 66, 70, 10, -56, -58, -11, -3, 3, 10, 19, 30, 37, -79, -62, -31, -15, 7, 12, 15, 19, 28, -43, -20, 14, 29, 42, 53, 58, 66, 74, 52, 71, 82, 82, 105, -54, -25, -8, 13, 18, 48, 56, 63, 71, 65, 71, 87, 96, 89, -66, -26, 0, 3, 4, 15, 20, 30, 37, 45, 41, 45, 57, 58, 67, -82, -14, 19, 39, 65, 100, 92, 122, 133, 139, 168, 160, 165, 187, 158, -31, -16, -8, -8, 18, 25, 23, 36, 41, 54, 59, 89, 69, 70, 77, 70, 71, 72, 81, 83, 81, 103, 115, 96, 101, 114, 103, 130, -53, -29, -8, 16, 46, 64, 59, 73, 63, 95, 86, 100, 124, 128, 118, 144, 136, 154, 147, 149, 153, 185, 169, 154, 145, 185, 185, 243, -1, -20, -11, -51, -6, -1, 49, 34, 31, 27, 120, 175, 8, 45, 1, -90, 11, 5, 20, 8, 49, 24, 6, 64, 74, 103, 66, 37, 45, 62, 143, 179, 3, 39, 48, 0, 54, 44, 71, 59, 36, 36});
 */
