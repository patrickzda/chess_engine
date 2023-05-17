package engine.move_generation;

import engine.representation.Direction;

import static engine.representation.Direction.*;

public class MoveMasks {

    // Arrays um alle Diagonalen abzuspeichern
    public long[] diagonals = new long[16];
    public long[] antiDiagonals = new long[16];
    public long[] horizontals = new long[8];
    public long[] verticals = new long[8];

    // einige Magic-Numbers
    private long DIAGONAL = -9205322385119247871L;
    private long ANTI_DIAGONAL = 72624976668147840L;
    private long MSB_ONE = -9223372036854775808L;
    private long HORIZONTAL = 255L;
    private long VERTICAL = 72340172838076673L;
    private long ZEROS = 0L;

    private long[][] storedRays = {
            {72340172838076672L, 144680345676153344L, 289360691352306688L, 578721382704613376L, 1157442765409226752L, 2314885530818453504L, 4629771061636907008L, -9187201950435737600L, 72340172838076416L, 144680345676152832L, 289360691352305664L, 578721382704611328L, 1157442765409222656L, 2314885530818445312L, 4629771061636890624L, -9187201950435770368L, 72340172838010880L, 144680345676021760L, 289360691352043520L, 578721382704087040L, 1157442765408174080L, 2314885530816348160L, 4629771061632696320L, -9187201950444158976L, 72340172821233664L, 144680345642467328L, 289360691284934656L, 578721382569869312L, 1157442765139738624L, 2314885530279477248L, 4629771060558954496L, -9187201952591642624L, 72340168526266368L, 144680337052532736L, 289360674105065472L, 578721348210130944L, 1157442696420261888L, 2314885392840523776L, 4629770785681047552L, -9187202502347456512L, 72339069014638592L, 144678138029277184L, 289356276058554368L, 578712552117108736L, 1157425104234217472L, 2314850208468434944L, 4629700416936869888L, -9187343239835811840L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, -9223372036854775808L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
            {-9205322385119247872L, 36099303471055872L, 141012904183808L, 550831656960L, 2151686144L, 8404992L, 32768L, 0L, 4620710844295151616L, -9205322385119248384L, 36099303471054848L, 141012904181760L, 550831652864L, 2151677952L, 8388608L, 0L, 2310355422147510272L, 4620710844295020544L, -9205322385119510528L, 36099303470530560L, 141012903133184L, 550829555712L, 2147483648L, 0L, 1155177711056977920L, 2310355422113955840L, 4620710844227911680L, -9205322385253728256L, 36099303202095104L, 141012366262272L, 549755813888L, 0L, 577588851233521664L, 1155177702467043328L, 2310355404934086656L, 4620710809868173312L, -9205322453973204992L, 36099165763141632L, 140737488355328L, 0L, 288793326105133056L, 577586652210266112L, 1155173304420532224L, 2310346608841064448L, 4620693217682128896L, -9205357638345293824L, 36028797018963968L, 0L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, -9223372036854775808L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
            {254L, 252L, 248L, 240L, 224L, 192L, 128L, 0L, 65024L, 64512L, 63488L, 61440L, 57344L, 49152L, 32768L, 0L, 16646144L, 16515072L, 16252928L, 15728640L, 14680064L, 12582912L, 8388608L, 0L, 4261412864L, 4227858432L, 4160749568L, 4026531840L, 3758096384L, 3221225472L, 2147483648L, 0L, 1090921693184L, 1082331758592L, 1065151889408L, 1030792151040L, 962072674304L, 824633720832L, 549755813888L, 0L, 279275953455104L, 277076930199552L, 272678883688448L, 263882790666240L, 246290604621824L, 211106232532992L, 140737488355328L, 0L, 71494644084506624L, 70931694131085312L, 69805794224242688L, 67553994410557440L, 63050394783186944L, 54043195528445952L, 36028797018963968L, 0L, -144115188075855872L, -288230376151711744L, -576460752303423488L, -1152921504606846976L, -2305843009213693952L, -4611686018427387904L, -9223372036854775808L, 0L},
            {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 0L, 516L, 1032L, 2064L, 4128L, 8256L, 16512L, 32768L, 0L, 132104L, 264208L, 528416L, 1056832L, 2113664L, 4227072L, 8388608L, 0L, 33818640L, 67637280L, 135274560L, 270549120L, 541097984L, 1082130432L, 2147483648L, 0L, 8657571872L, 17315143744L, 34630287488L, 69260574720L, 138521083904L, 277025390592L, 549755813888L, 0L, 2216338399296L, 4432676798592L, 8865353596928L, 17730707128320L, 35461397479424L, 70918499991552L, 140737488355328L, 0L, 567382630219904L, 1134765260439552L, 2269530520813568L, 4539061024849920L, 9078117754732544L, 18155135997837312L, 36028797018963968L, 0L},
            {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 257L, 514L, 1028L, 2056L, 4112L, 8224L, 16448L, 32896L, 65793L, 131586L, 263172L, 526344L, 1052688L, 2105376L, 4210752L, 8421504L, 16843009L, 33686018L, 67372036L, 134744072L, 269488144L, 538976288L, 1077952576L, 2155905152L, 4311810305L, 8623620610L, 17247241220L, 34494482440L, 68988964880L, 137977929760L, 275955859520L, 551911719040L, 1103823438081L, 2207646876162L, 4415293752324L, 8830587504648L, 17661175009296L, 35322350018592L, 70644700037184L, 141289400074368L, 282578800148737L, 565157600297474L, 1130315200594948L, 2260630401189896L, 4521260802379792L, 9042521604759584L, 18085043209519168L, 36170086419038336L},
            {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 2L, 4L, 8L, 16L, 32L, 64L, 0L, 256L, 513L, 1026L, 2052L, 4104L, 8208L, 16416L, 0L, 65536L, 131328L, 262657L, 525314L, 1050628L, 2101256L, 4202512L, 0L, 16777216L, 33619968L, 67240192L, 134480385L, 268960770L, 537921540L, 1075843080L, 0L, 4294967296L, 8606711808L, 17213489152L, 34426978560L, 68853957121L, 137707914242L, 275415828484L, 0L, 1099511627776L, 2203318222848L, 4406653222912L, 8813306511360L, 17626613022976L, 35253226045953L, 70506452091906L, 0L, 281474976710656L, 564049465049088L, 1128103225065472L, 2256206466908160L, 4512412933881856L, 9024825867763968L, 18049651735527937L},
            {0L, 1L, 3L, 7L, 15L, 31L, 63L, 127L, 0L, 256L, 768L, 1792L, 3840L, 7936L, 16128L, 32512L, 0L, 65536L, 196608L, 458752L, 983040L, 2031616L, 4128768L, 8323072L, 0L, 16777216L, 50331648L, 117440512L, 251658240L, 520093696L, 1056964608L, 2130706432L, 0L, 4294967296L, 12884901888L, 30064771072L, 64424509440L, 133143986176L, 270582939648L, 545460846592L, 0L, 1099511627776L, 3298534883328L, 7696581394432L, 16492674416640L, 34084860461056L, 69269232549888L, 139637976727552L, 0L, 281474976710656L, 844424930131968L, 1970324836974592L, 4222124650659840L, 8725724278030336L, 17732923532771328L, 35747322042253312L, 0L, 72057594037927936L, 216172782113783808L, 504403158265495552L, 1080863910568919040L, 2233785415175766016L, 4539628424389459968L, 9151314442816847872L},
            {0L, 256L, 66048L, 16909312L, 4328785920L, 1108169199616L, 283691315109888L, 72624976668147712L, 0L, 65536L, 16908288L, 4328783872L, 1108169195520L, 283691315101696L, 72624976668131328L, 145249953336262656L, 0L, 16777216L, 4328521728L, 1108168671232L, 283691314053120L, 72624976666034176L, 145249953332068352L, 290499906664136704L, 0L, 4294967296L, 1108101562368L, 283691179835392L, 72624976397598720L, 145249952795197440L, 290499905590394880L, 580999811180789760L, 0L, 1099511627776L, 283673999966208L, 72624942037860352L, 145249884075720704L, 290499768151441408L, 580999536302882816L, 1161999072605765632L, 0L, 281474976710656L, 72620543991349248L, 145241087982698496L, 290482175965396992L, 580964351930793984L, 1161928703861587968L, 2323857407723175936L, 0L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}
    };

    // generiert alle möglichen horizontalen, vertikalen, Diagonalen und Anti-Diagonalen einmalig, um so schnell
    // die Rays zu berechnen
    public MoveMasks() {
        for (int i = 0; i < 8; i++) {
            // für die Horizontalen 255 (0b11111111) um ein vierlfaches von 8 shiften
            horizontals[i] = (HORIZONTAL << i * 8);

            // für die vertikalen 72340172838076673L (einer vertikale reihe aus einsen) um 0-7 shiften
            verticals[i] = (VERTICAL << i);

            // für die Diagonalen wird einfach die haupt- bzw. Anti-Diagonale geshiftet
            diagonals[i] = (DIAGONAL << (8 * i));
            antiDiagonals[i] = (ANTI_DIAGONAL >>> (8 * i));
        }

        // aus Gründen der Diagonal-Berechnung aus dem Index muss die 9. Diagonale (index 8) leer sein
        diagonals[8] = ZEROS;
        antiDiagonals[8] = ZEROS;

        for (int j = 9; j < 16; j++) {
            // Diagonalen auch in die andere Richtung shiften, da es insgesamt 15 diagonalen gibt
            // (16, wenn man den Index 8 mitzählt)
            diagonals[j] = (DIAGONAL >>> (8 * (8 - j)));
            antiDiagonals[j] = (ANTI_DIAGONAL << (8 * (8 - j)));
        }
    }

    // Bitmaske, die nur in nördlicher Richtung von dem Ausgangsindex einsen hat
    public long maskNorth(int index) {
        // ein Edge-Case muss abgefangen werden, wenn das Feld das MSB ist, sonst wird die Maske auf nur einsen
        // gesetzt und die Richtung wird nicht abgeschnitten.
        // TODO: evtl. bessere Lösung ohne if-Statement finden
        if (index == 63) {
            return 0L;
        }

        return MSB_ONE >> (62 - index);
    }

    // Bitmaske, die nur in östlicher Richtung von dem Ausgangsindex einsen hat
    public long maskEast(int file) {
        // ein Edge-Case muss abgefangen werden, wenn das Feld am östlichen Rand liegt, sonst wird das
        // Feld auf dem der Spielstein steht auch als mögliches Zielfeld mit ausgegeben
        // TODO: evtl. bessere Lösung ohne if-Statement finden
        if (file == 7) {
            return 0L;
        }

        long mask = MSB_ONE >> (6 - file);
        mask = mask | (mask >>> 8);
        mask = mask | (mask >>> 16);
        return mask | (mask >>> 32);
    }

    // Bitmaske, die nur in südlicher Richtung von dem Ausgangsindex einsen hat
    public long maskSouth(int index) {
        return ~(MSB_ONE >> (63 - index));
    }

    // Bitmaske, die nur in westlicher Richtung von dem Ausgangsindex einsen hat
    public long maskWest(int file) {
        long mask = MSB_ONE >> (7 - file);
        mask = mask | (mask >>> 8);
        mask = mask | (mask >>> 16);
        return ~(mask | (mask >>> 32));
    }

    public long rays(Direction dir, int index) {
        if(index == 64 || index == -1){
            return 0;
        }
        return storedRays[dir.ordinal()][index];
    }

    // Berechnet von einem Feld auf dem Schachbrett eine Bitmaske, die nur einsen von diesem Feld aus in die
    // angegebene Richtung hat, wobei auf dem übergebenen Feld selber eine Null steht
    public long slowRays(Direction dir, int index) {
        if(index == 64 || index == -1){
            return 0;
        }

        long mask, res;
        int rank, file;
        switch(dir) {
            case NORTH:
                file = index & 7;   // = index % 8
                mask = maskNorth(index);

                res = verticals[file] & mask;
                break;

            case NORTH_EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskNorth(index);

                res = diagonals[(rank - file) & 15] & mask;
                //              ^^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte Diagonale
                break;

            case EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskEast(file);

                res = horizontals[rank] & mask;
                break;

            case SOUTH_EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = antiDiagonals[(rank + file) ^ 7] & mask;
                //                  ^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte anti-Diagonale
                break;

            case SOUTH:
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = verticals[file] & mask;
                break;

            case SOUTH_WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = diagonals[(rank - file) & 15] & mask;
                //              ^^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte Diagonale
                break;

            case WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8

                mask = maskWest(file);

                res = horizontals[rank] & mask;
                break;

            case NORTH_WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8

                mask = maskNorth(index);

                res = antiDiagonals[(rank + file) ^ 7] & mask;
                //                  ^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte anti-Diagonale
                break;

            default:
                // dieses Statement dürfte nie erreicht werden! Aus komplettheitsgründen wird in dem Fall
                // eine Fehlermeldung ausgegeben und eine leere Bitmaske zurückgegeben
                System.out.println("irgendwas ist gewaltig schief gelaufen...");
                res = ZEROS;
                break;

        }
        return res;
    }

    // Berechnet für ein Feld auf dem Schachbrett Rays in alle Richtungen.
    // Kann z.B. für die Zugmöglichkeiten der Dame benutzt werden
    public long allDirections(int index) {
        // verodert alle Richtungen von einem Index
        return rays(NORTH, index) |
               rays(NORTH_EAST, index) |
               rays(EAST, index) |
               rays(SOUTH_EAST, index) |
               rays(SOUTH, index) |
               rays(SOUTH_WEST, index) |
               rays(WEST, index) |
               rays(NORTH_WEST, index);
    }

    // für Debugging-Zwecke. Gibt das Bitboard in der Reihenfolge von einem Schachbrett aus
    public static void printBitBoard(long bitBoard) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int i = (7 - x) * 8 + y;
                if ((bitBoard & (1L << i)) == 0) {
                    System.out.print("0");
                }
                else {
                    System.out.print("1");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
