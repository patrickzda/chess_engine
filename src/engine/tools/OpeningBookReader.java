package engine.tools;

import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/*
Die Datei mit den Eröffnungen muss wie folgt formatiert sein:
- Jede Zeile besteht aus einer 23-stelligen HEX Zahl
- Bits 0 bis 15: Zobrist-Key der Stellung (in HEX)
- Bits 16 und 17: Index des Startfeldes des besten Zuges (in HEX)
- Bits 18 und 19: Index des Endfeldes des besten Zuges (in HEX)
- 20. Bit: Repräsentiert die Figur, die bewegt werden soll (0: König, 1: Dame, 2: Turm, 3: Läufer, 4: Springer, 5: Bauer)
- 21. Bit: Handelt es sich um eine Rochade? 0: Nein, 1: Ja
- 22. Bit: Handelt es sich um eine Bauernumwandlung? 0: Nein, 1: Zur Dame, 2: Zum Turm, 3: Zum Läufer, 4: Zum Springer
- Zahlen, die weniger Bits benötigen als Platz vorhanden ist, erhalten führende Nullen
- Alle Zeilen sind nach der ersten 16-Bit-Zahl geordnet, beginnend mit der Kleinsten
- Am Ende jeder Zeile ist ein Newline-Char: \n
 */

public class OpeningBookReader {
    String pathToFile;
    RandomAccessFile openingBook;
    boolean allowEditing, initialized = false;
    long numberOfLines;
    private final int LINE_LENGTH = 24;
    private final long BLACK_TO_MOVE = 2291423046773879668L, WHITE_KING_MOVED = 377209881458749858L, BLACK_KING_MOVED = 9161379089589498228L, WHITE_SHORT_ROOK_MOVED = -2957370995913479111L, WHITE_LONG_ROOK_MOVED = -8036376284382405704L, BLACK_SHORT_ROOK_MOVED = -4567700138574629895L, BLACK_LONG_ROOK_MOVED = 5938326843596246216L;
    private final long[][] ZOBRIST_VALUES = new long[][]{
        {-8381899916751569004L, -2486605555524057662L, -9168123647907970564L, -4824191285306832205L, -8849517061691484790L, 5994676847265104746L, -7349531603057863392L, -8672641378056022241L, 3839482552214473249L, -4241456175358871621L, 890047877241687330L, 7804659745106285486L},
        {1923039206319848929L, 1731424163672396031L, 7532337301004928629L, -2841834755016874589L, 5815652844659482055L, 4223896180353360147L, 4289298402853403968L, 5943642974781209368L, 1249314909288562115L, 3509673629802183656L, -6161446146224320547L, 6270045719509537142L},
        {-6889471611741830341L, -7502702509644863994L, -8913550420574580388L, -8977679527123366913L, 5827499177732792198L, 5153094816178230020L, 1943817335680510974L, 114843908768635461L, -8499456263172875662L, -7220170394421824495L, 8046224875323036602L, -4522732461215961425L},
        {3303539592520068287L, 8199300246015235694L, 4315259651962554555L, -6795214369889581829L, -8745376023361885534L, -2830583677497847310L, 3112972472400718303L, 5999212176081991862L, -1263654728446640573L, 226289780906592133L, 2922891841880708922L, 6931101331121089478L},
        {-5310178200175562177L, 1195449752401080229L, -362921232787135343L, -9176540973763592991L, 8065755400400751654L, -2391482819900131100L, -8274659552115008544L, 1617391665837559315L, -6816303987681478914L, 3444446205981685351L, 2638329998246301652L, 8077225367781956412L},
        {1537634611631373758L, 1741919328145645161L, 8922457568259587666L, 4381623569072152720L, 8030844168430130644L, -2910644192871819082L, 3230857157256970990L, 7874321044267094293L, 7566327422156585376L, -909904309279843024L, 1769240337998649717L, -7537200310942180056L},
        {2101295073386042103L, -6253163806476983730L, -9222616162616774556L, -3076297165312538376L, 1830579491296736672L, -7558440848544293992L, 6120432646442584942L, 170489609763754762L, 8825730721517660634L, -4428722066480773076L, -2367353120936495623L, 3098266337107602099L},
        {4220714797334546133L, 39432957646950716L, 3788735414136014241L, -110106341680351583L, -459289733228094156L, 2126752242110036110L, 3033360913546996916L, 1444943845740637011L, -790195432057704047L, 750142867189929722L, 6002858462511209653L, -2369488336568684132L},
        {-7483809769499539719L, -4384215309161501968L, 6919130011534063834L, 4622350486423690532L, 809524470010280300L, -7483219808911509776L, -6473870621615892338L, -4991820145108962585L, 7708861812618651742L, 229497597919604751L, -1215414913764664849L, 8855072972794462132L},
        {-2412727521397509831L, 3913104217376242808L, -5755896775738424239L, 9117503332968464047L, -7594653907614942759L, 2400700113199366633L, -6003430391072146759L, 7614215527627165647L, -2551848421148472562L, -4540441101059013582L, -6654507101688136094L, -4124740063555748256L},
        {-4895073404165162210L, 2968537403791381434L, 1027232961525383484L, -5302401877993875581L, 9115482020694566976L, -2197265812884108294L, 3021724116993771624L, 6642383544581001385L, 2606213249167115041L, -2740350027457175041L, 9026505581681319613L, -6324847121298014692L},
        {4769378569284200803L, 8367905293041533676L, -3967193201225787578L, 8653361140095064439L, 3571950228095782937L, -2702870066263653770L, -2464604194319769662L, 7702797718371735592L, 5148334809590152781L, 2809452314845190502L, 2394710850951815971L, 3431928650555675125L},
        {-2092360904562026629L, -6400682327155084261L, -6800021716969276401L, -8766062679281338508L, 8755328582946616851L, -2079464865400811259L, 8972333753331283810L, -6374263528976181103L, 7942727578940397050L, -1550158284211228885L, 43445199987189418L, 5109659631773793036L},
        {7930482991546453769L, -3405380023930418010L, 7350108174432141994L, -1059615997425041160L, 4440848187780325261L, 3537341184833991972L, 3400158866826485207L, 1697363224569519934L, 7122609854799546053L, 531277412774223974L, 5008405716364830430L, -5576484662841842664L},
        {-2881330156916150424L, 3069645525195481282L, -6027086651288012004L, 1078066270923853206L, 197136921795914268L, 4289196684345975201L, 3913019692643322148L, -550508442377637860L, 9009033884670867456L, -1728800730841074174L, -20213021692263619L, 3495039985401294050L},
        {-2643185578115848925L, -418488569498175755L, -7869816123020287518L, -3035481014210217869L, 9103868974225847050L, -3982074007414803935L, -9158555101433874880L, 2459019656431366446L, -6050902015410040018L, 4873474005084465216L, -1308729643298992965L, -5646902788687641144L},
        {-6995469904104803972L, 528667564452223901L, 86783059364503401L, 6348330479018798024L, -593280389287735597L, -7619750964517966179L, 8288578470261823637L, 6942758628856374794L, 90523081404361609L, 1644020719396213299L, 8109751550119306558L, -599304481835148483L},
        {-9074772361120333337L, -4997347629365787543L, -1836085282188716139L, 8030174236825287781L, 7903214794749579432L, 4728567356292701918L, -2981147249679476349L, -1033892049764287655L, -6865644255279975042L, -2179183073904921277L, -4771531856493023456L, 4603850942654091590L},
        {-459047464203233395L, 4270228172404409309L, 4136685586753669437L, 1566954480982365482L, -8430322793357123090L, -1859295864175284730L, 723939319137626075L, -3278038016760241574L, 6411612126909238830L, -7670403932061070164L, 5318592883555011762L, 2304888127145234172L},
        {1105241064021229260L, -4489232959515554607L, 3281448771351592363L, 7390651835432743566L, 7087256654338795966L, 5370071544251653641L, -1173266738143455386L, 64473733017648221L, 6377018316220650221L, -8667593449202732801L, -7748212762111217739L, 5464961438040381967L},
        {-8107612607978252620L, -3749620760832555536L, 1024798218460940319L, -2190964254832103910L, -6099237494733781915L, 8512571861435173678L, -1527279223934349218L, -8571936510057802166L, -9039879604649899836L, 5302261592728357790L, 1811674462338988257L, -6570920126775016897L},
        {5051669615978080394L, -1355410214208017093L, -8663435134735280821L, 4007349342687369708L, -2770483035950216477L, 2223187285142323089L, 6111013781822279410L, -1891165585626418150L, -3889044128020540432L, 6877083344982727234L, 588505815143065441L, -5334964018415428381L},
        {-2829856954960883898L, -7389293125744997499L, -2802546255509239082L, -5339065588931220425L, -2827567315288888722L, -8434909465953085790L, 5794350485255615173L, 1227993293315648602L, -5409774502216286365L, -8547405905242586505L, -5706688379670194093L, -7301369112009224092L},
        {-3597312165757210857L, -8318735079173883914L, 8025042809934083803L, 5068765876679441087L, -8716564519521793043L, 5454175614513688740L, -5900365313315266963L, -8361543949540797523L, -4668907991571065792L, -7035591263237573348L, 1940696935289869898L, 4559582027817037760L},
        {3561479509639855323L, 4675241081680931722L, -3634454577405800599L, -820645438869354912L, 2928005084843789184L, -7907592532160514511L, -4656303759078086922L, -6417398736789292983L, -4582819822266498718L, -2312179846116335131L, 4616857072681072845L, -738439893255461366L},
        {-904340327029631574L, 6461814011426825362L, 6521164954544751107L, -340060329586813306L, 1056039823150989301L, -2963594274069156941L, 8457085466703254366L, 7927855401458232695L, -4958394293797514540L, 3128945458998360795L, -7244451045195738152L, 8837304854444847590L},
        {-5277052099065567670L, -1564718136998110672L, -4640842946702126304L, -8813341711442647884L, 9021636755621909074L, 1780202573876122858L, 35121733886405656L, -3551615922809034987L, -7300417827396028073L, -8931163291844376615L, 7584311107383326552L, -4562871002980880112L},
        {1911733585693893306L, 2468453663185944541L, -1495565659875521110L, -7649090170288859136L, -8483975418818697904L, 2525682394123937899L, 6221963049661094540L, -2922047959300430570L, 6147687159901495777L, 2206564986538199567L, -1942197646873674063L, 3156548719041248212L},
        {-3332158882078126678L, 2041649400606709988L, -8514487616974798208L, -2895943322075672429L, 3653218772680477916L, 185964423616533278L, -8373802937352644461L, 5970071178599526142L, -5086808503171162853L, 6847242791458697088L, 2907281732431343674L, 5663509853963931797L},
        {-1567183677467144193L, 6680275524486421478L, -2257387255189841930L, -7042727590623345877L, 4537381423547372950L, 3157070897407773629L, -3377659289228152834L, 8880359809851416425L, -3752570968640432416L, 1518768859817793158L, -1032677984855561027L, -4667430997724754103L},
        {-7904180955353685934L, -1073282199656815017L, 7371024163392448849L, -3585849938410281829L, -4215525753768764332L, -4459751714621835687L, 1641231085885786638L, 1522137358582906755L, 2160345278117551555L, -4841361164338208437L, -7109283858141057287L, 3711301382532482297L},
        {8111575851287542383L, 2561538598985515005L, 4325374825055057486L, 2464795495695000903L, 2655366354785449374L, -808262264437123114L, -1088989882513699077L, -1611963525715447153L, -3312068863693587832L, -7739156202577553073L, -1062328911253513438L, -4203853794085599421L},
        {4446315177098651352L, 5106785206223449718L, -3694043426952846693L, -1801469349721998165L, 3753770539485766961L, -8778348170490242708L, 4082530996644222319L, 6634114255487644003L, 3883730041482647463L, 118733232894223079L, 947392297445973598L, 996918331812700121L},
        {-4339702251195280830L, 5932457547542501553L, 5553399655033734490L, 2828255898385220819L, 2594200026471048575L, 5098433261163502631L, 3057131374487937546L, -5756997912211434008L, -2668148206432913202L, -448416076776718150L, -2805363136380117432L, -7024725671612807166L},
        {-5513768938352659179L, -4042744232658683278L, -6216009161665378141L, 2618547617011131874L, 2305818363943190315L, -7162629101196944282L, 6326184173326694110L, -8627693565259893092L, -981786791492631970L, -38347605107562683L, -6038315633175185297L, 7805525588371239958L},
        {-2042268838914782482L, -4679194240614914096L, -7453849245230524159L, -140165911108922994L, 3026427008317862031L, 1573601427437479261L, -5384696313192320270L, 3364461033445003793L, 7408832124704903403L, -6081810918384426922L, -7046517642924764714L, -4902937561454104827L},
        {-8382637971095184866L, -1999162094162485064L, 96954121412732146L, -8905864957436572000L, -1115186692071201480L, -9014556736407677803L, 6000848195479454146L, 6658014236555676270L, -5014110497760370497L, -4149866237209739630L, 407915473534514803L, 6566730795164304847L},
        {-2478504813199750358L, -3806363596554685335L, -3756717866377909143L, 7013393609942914166L, 8845540982886514023L, 3142996215117147496L, -975709288321738053L, 7889784766712754411L, 1355118970858505557L, -8102555360162619151L, 1031430945831520434L, 3157745424732226314L},
        {1256968136839041099L, 1777862494547637496L, 5793861741471768654L, 1313568253288822402L, 8822891636696901008L, 6960426649650691208L, -7231444918420254527L, 4830103607209222487L, 1891416004044533984L, 815019377961190974L, 1317278316449661169L, 7150959205626067553L},
        {8542506415660686573L, -4109187863072474420L, 3512340854627029498L, -8390134570972618805L, -2323433008227189284L, -1128999078030062728L, -5481143740122680918L, 9044128564412870036L, 8111976254896704452L, -483556668844445542L, 8743344820295843076L, 5781314860393358353L},
        {9019841425256475955L, 2115553160892971041L, 4576042344537453246L, -2669436946623963543L, -1184636590144990808L, -1871961768434801649L, -5929219053268322878L, 4409814227983603992L, 5992907631153536026L, 9168267347931994361L, 5902473629751418800L, 511703732106184813L},
        {-2460653185558438545L, -2116981158699524984L, 6068748570535633755L, -5871915307368104181L, -7444372780392030458L, 7930878194128430074L, 8618453955447553864L, 3439165350912648300L, -1777393834554114770L, -8016625922582416735L, -6952314707316277198L, -934753520335029415L},
        {1213536900069298604L, -2890578881768454045L, 8832498281567720328L, 5933325236266219635L, 1813080488744562233L, -3844964760906588104L, 2494021776017336813L, 7258785975401719982L, 3369071135862679298L, -5548103504926736720L, 8837883543602227640L, 545410336577382352L},
        {3171940284556012839L, -6366953564033544086L, -3196127507639946894L, 8607814769902724855L, 3492833109899999548L, -1661225090453546850L, -1654818564287731110L, 3274034615479239440L, 2442238641762190794L, -1596620050465804396L, -7605680012946035996L, 2229998701434514272L},
        {-442902942591460913L, 3102571786952823083L, -4602380636794803915L, -1042336230911846401L, 2331428709463434042L, 7342074686578375507L, -4044316552891138647L, 3270173661779759192L, -5152498960197295760L, 7043709410069503123L, -8026615244959337138L, -282436298342678101L},
        {-5737891292809207862L, 3885687481056105092L, -9016900620029381530L, -8413369487651560052L, -7373841752838438892L, -6384941800444037037L, 4378811437222586601L, -4837638507549853599L, -3702725769162615536L, -3988707776829225661L, -7470244647203549952L, 7607154516774846439L},
        {1964821257579138289L, -13556119755719895L, 8872958989034109323L, 1952147194106896813L, 7443858510828760033L, 2128971630668850414L, 8268363033120278685L, -3483963682869065321L, -4555300590082746757L, 4430467554441880849L, 8744383930737166330L, 656617264398557276L},
        {5804444566710283345L, -4664847246211640286L, 6813559642837662624L, 2669137369034330633L, 3838888308251800167L, -4228029818242879672L, 4057241625691582523L, -4824695825452522371L, -6468489362855637578L, 5554264109126145724L, -1130386746706387537L, -7419794278959276565L},
        {2549478478486654378L, 2459348737422695500L, 8830425423864633746L, -8122235020823594150L, 3916535726285145765L, -6587931980038692391L, -9108644302306666066L, -8614126767179417816L, 5499603157597715578L, -7445174293763979303L, 6770718333636150916L, -4618807271277781117L},
        {9004722621615791346L, -260576783472546858L, -166933287864164924L, 4823342932262419695L, -6417623932565140918L, 3274317520974821100L, -452964229307505449L, -6632607607847221053L, 2779174569210394035L, -9140981446492029873L, 8577069721663794005L, -3423543964415544405L},
        {-8090113232530863663L, -3677738032435517501L, -7390125459672310385L, -313531014903022553L, 4614470116688422205L, 5010925275732196386L, 4499902623193818886L, -7456670383364336373L, 5974552806613198083L, -8657137109586419238L, 7979442333224199409L, 8245333004067478525L},
        {9012427454331240228L, -7600607453429889684L, 8437940068662503612L, 808816155534467067L, -7384584316531610347L, 1403228371560646126L, -3323301022026364285L, -8508024740543907886L, 4478256196217480767L, 333593916554326922L, 5926029723980172187L, -5880955306131003224L},
        {-9046578564390655363L, 11839394919177725L, 7698572814230767882L, 8131173014838942834L, 4539539585121475745L, -5512342990887767271L, -7657703765927264758L, 3352804240321112190L, 8115834689290054895L, 6213519458207477059L, -2627134517099538806L, -8743114806621821973L},
        {1628688204634248095L, -4577199782056177163L, -2284184872652385875L, 6161334864994586156L, -2948616280269513889L, -8467863984789192643L, -4294382127689862072L, -8263354515278051958L, -1031134804732642352L, -3359022617568604355L, -1540966772656354456L, 536052577983188383L},
        {-8351740747711364091L, -326518605042205527L, 4333078640973889226L, -3912018690547128869L, 3513907691884221703L, 2048088874137447244L, -3166620586026290334L, -6407366469018914814L, 7810173675362069587L, 7515495463969698179L, 1426282488263179733L, -7774924976012974421L},
        {-3268335929721029031L, 8807669495965693888L, 165873672746214654L, -9040293555113991229L, 6286904288775938303L, 1308625964760295176L, -9213926040156801939L, 1834877724976559880L, -3134427304696052195L, 5985322624825059190L, 5381109520446349027L, 2985111981202885775L},
        {-3693498821102729729L, 8751045595012722358L, 84610103218548951L, 5667757087388345151L, -1120399306884892186L, 8655623629440441994L, 6790950838769160948L, -3649346702840032428L, -5266855052780263801L, -1764172408073407671L, -6838706238709998439L, 6234095655231792348L},
        {7178460946270697098L, 4180949351196312155L, -5644262165900151209L, 999109287032862269L, 2808183303398491404L, 3900725310334552254L, -2065831477867049865L, -2605403605275446611L, -6574913983621138403L, 5578025373455239556L, 6476203109426450802L, -3406842485946443591L},
        {1122864816815418179L, -8827339976281637805L, -217014646497341322L, 2325363680928108735L, 3770649120362670582L, 5671299370188180451L, -7607664167262892056L, 547535223861859187L, 4976462407934409250L, -3318368942492490620L, 3422064429431342044L, -4537623453440401314L},
        {-1211933964684495192L, -721183587065212267L, 193624894702833823L, -8922542530006285222L, 5524236166843824604L, -7327451733390806770L, 6809974927750126894L, 8257003069304877079L, -8310966670699800568L, 5480284892921128951L, -6428310982781897284L, 4294272322624003738L},
        {-4333747346910139928L, 566328983385012974L, 4189623075356065328L, 3762871755577625529L, 3551932494279272237L, 8181963922946779046L, 7010537597662082212L, -7325487819269424480L, 7538602774827192570L, 5255851739609093218L, -7609927718986923033L, -4870182534922007474L},
        {4535657899000966250L, -3940893933631369602L, 7109074579303212540L, -3929122133029460455L, 846525854532717832L, 1097296202719516903L, -3973406822287665511L, 2248589794572691494L, -3780631411333828779L, -7579530099731559779L, -5512979720002120279L, 453757705693000690L},
        {-707044078820662459L, 7106994783133269304L, 2719919016115993035L, -1195979801044811060L, 7438224331017586370L, 7044591434703719264L, -106603299849235760L, 7881702172142292056L, -8476023460243992536L, 4166071740959022932L, -784727019257357248L, 6606029992526920973L},
        {1957781268342810823L, -5776858337363177116L, 1683289238697263062L, -2739266481253925704L, 3068072222673524581L, -8545495212854045065L, 8745514965255219200L, 617457514452862199L, -3910686322097571143L, -1907895869180739962L, -645531415235485088L, -4980366191683188801L}
    };

    public OpeningBookReader(String pathToFile, boolean allowEditing) {
        this.pathToFile = pathToFile;
        this.allowEditing = allowEditing;

        try {
            if(allowEditing){
                openingBook = new RandomAccessFile(pathToFile, "rw");
            }else{
                openingBook = new RandomAccessFile(pathToFile, "r");
            }

            numberOfLines = openingBook.length() / LINE_LENGTH;
            initialized = true;
        }catch (IOException e){
            initialized = false;
            System.out.println(e.toString());
        }
    }

    public Move readBestMove(Board board) throws IOException {
        long bottom = 0, top = numberOfLines;
        long middle;
        byte[] currentLineBuffer = new byte[LINE_LENGTH];

        if(!initialized){
            return null;
        }

        long boardKey = generateKey(board);

        while(bottom <= top){
            middle = (bottom + top) / 2;
            openingBook.seek(middle * LINE_LENGTH);
            openingBook.read(currentLineBuffer);

            String currentLine = new String(currentLineBuffer);

            int comparison = Long.compareUnsigned(Long.parseUnsignedLong(currentLine.substring(0, 16), 16), boardKey);

            if(comparison == 0){
                Move move = new Move(Integer.parseInt(currentLine.substring(16, 18), 16), Integer.parseInt(currentLine.substring(18, 20), 16), PieceType.values()[Integer.parseInt(currentLine.substring(20, 21), 16)]);
                if(Integer.parseInt(currentLine.substring(21, 22), 16) == 1){
                    move.isCastling = true;
                }

                int promotionIdentifier = Integer.parseInt(currentLine.substring(22, 23), 16);
                if(promotionIdentifier == 1){
                    move.isPromotionToQueen = true;
                }else if(promotionIdentifier == 2){
                    move.isPromotionToRook = true;
                }else if(promotionIdentifier == 3){
                    move.isPromotionToBishop = true;
                }else if(promotionIdentifier == 4){
                    move.isPromotionToKnight = true;
                }

                return move;
            }else if(comparison < 0){
                bottom = middle + 1;
            }else{
                top = middle - 1;
            }
        }

        return null;
    }

    public void writeBestMove(Board board, Move bestMove) throws IOException {
        long newKey = generateKey(board);

        if(!initialized){
            return;
        }

        long insertionIndex = 0;
        openingBook.seek(0);
        String currentLine = openingBook.readLine();

        while(currentLine != null && currentLine.length() > 16 && Long.compareUnsigned(Long.parseUnsignedLong(currentLine.substring(0, 16), 16), newKey) < 0){
            insertionIndex = insertionIndex + LINE_LENGTH;
            currentLine = openingBook.readLine();
        }

        if(currentLine != null && Long.compareUnsigned(Long.parseUnsignedLong(currentLine.substring(0, 16), 16), newKey) == 0){
            System.out.println("Der Wert ist bereits in der Datenbank vorhanden: " + board.toFENString());
            return;
        }

        insertStringAt(generateEntry(newKey, bestMove), insertionIndex);
        numberOfLines = numberOfLines + LINE_LENGTH;
    }

    private void insertStringAt(String value, long index) throws IOException {
        if(allowEditing){
            RandomAccessFile temp = new RandomAccessFile(new File("opening_book.txt~"), "rw");
            long fileSize = openingBook.length();

            if(fileSize == 0){
                openingBook.seek(0);
                openingBook.write(value.getBytes());
                return;
            }

            FileChannel source = openingBook.getChannel(), target = temp.getChannel();

            source.transferTo(index, (fileSize - index), target);
            source.truncate(index);
            openingBook.seek(index);
            openingBook.write(value.getBytes());
            long newIndex = openingBook.getFilePointer();
            target.position(0L);
            source.transferFrom(target, newIndex, fileSize - index);
            target.close();
        }
    }

    private String generateEntry(long newKey, Move bestMove){
        String value = String.format("%1$016X", newKey) +  String.format("%1$02X", bestMove.getStartFieldIndex()) + String.format("%1$02X", bestMove.getEndFieldIndex());
        value = value + bestMove.getPieceType().ordinal();

        if(bestMove.isCastling){
            value = value + "1";
        }else{
            value = value + "0";
        }

        if(bestMove.isPromotionToQueen){
            value = value + "1";
        }else if(bestMove.isPromotionToRook){
            value = value + "2";
        }else if(bestMove.isPromotionToBishop){
            value = value + "3";
        }else if(bestMove.isPromotionToKnight){
            value = value + "4";
        }else{
            value = value + "0";
        }

        value = value + "\n";
        return value;
    }

    private long generateKey(Board board){
        long key = 0L;

        if(board.getTurn() == Color.BLACK){
            key = key ^ BLACK_TO_MOVE;
        }

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((board.whitePieces & board.pawns & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][0];
                }else if((board.whitePieces & board.knights & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][1];
                }else if((board.whitePieces & board.bishops & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][2];
                }else if((board.whitePieces & board.rooks & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][3];
                }else if((board.whitePieces & board.queens & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][4];
                }else if((board.whitePieces & board.kings & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][5];
                }
            }else{
                if((board.blackPieces & board.pawns & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][6];
                }else if((board.blackPieces & board.knights & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][7];
                }else if((board.blackPieces & board.bishops & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][8];
                }else if((board.blackPieces & board.rooks & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][9];
                }else if((board.blackPieces & board.queens & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][10];
                }else if((board.blackPieces & board.kings & index) != 0){
                    key = key ^ ZOBRIST_VALUES[i][11];
                }
            }

            if(board.getHasWhiteKingMoved()){
                key = key ^ WHITE_KING_MOVED;
            }
            if(board.getHasBlackKingMoved()){
                key = key ^ BLACK_KING_MOVED;
            }
            if(board.getHasWhiteShortRookMoved()){
                key = key ^ WHITE_SHORT_ROOK_MOVED;
            }
            if(board.getHasBlackShortRookMoved()){
                key = key ^ BLACK_SHORT_ROOK_MOVED;
            }
            if(board.getHasWhiteLongRookMoved()){
                key = key ^ WHITE_LONG_ROOK_MOVED;
            }
            if(board.getHasBlackLongRookMoved()){
                key = key ^ BLACK_LONG_ROOK_MOVED;
            }
        }

        return key;
    }

}
