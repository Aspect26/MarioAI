package cz.cuni.mff.aspect.mario.controllers

class EvolvedControllers {

    companion object {
        fun jumpingSimpleANNController(): MarioController = simpleANNController(this.jumpingSimpleANNControllerWeights)
        fun actionsConstrainedANNController(): MarioController = simpleANNController(this.actionConstrainedANNControllerWeights)
        fun currentBestANNController(): MarioController = simpleANNController(this.currentBestANNControllerWeights)

        private fun simpleANNController(networkWeights: DoubleArray): MarioController {
            val network = SimpleAgentNetwork()
            network.setNetworkWeights(networkWeights)
            return SimpleANNController(network)
        }

        private val jumpingSimpleANNControllerWeights = doubleArrayOf(-0.032419545345660605, 0.02467506005854725, -0.1833763935635968, 0.8395610114542231, -0.30189860979756333, 0.5888572075756906, 0.6018419334196079, -0.05014307918336569, 0.8347901477382944, 0.29218014096871836, 0.02482738540827345, 0.19323386491796257, -0.9135324401511957, 0.5142486378269651, 0.5167066158272442, 0.3271426599633547, 0.3059708409599986, -0.15131327957438678, -0.7494497400854356, -0.4912056027914238, -0.08554635045573522, 0.38523125539378045, -0.468919297144029, -0.5959537062689975, 0.3146338213996358, 0.059684255598890124, 0.7552580033454139, 0.6458569717650398, -0.9774774994564948, 0.0669690723398364, 0.08302570166022405, 0.644743206644995, 0.34335654694742357, -0.4276996649361564, 0.49332347759121764, -0.4168403047688325, 0.051921444841060094, -0.5424701731232446, 0.8495249591384928, 0.6683241422079682, -0.062442201152386634, 0.13633051556216036, 0.6633941118877478, -0.6385375563838098, -0.8270176958880975, -0.2916968891783738, -0.6142971825090702, 0.6098537320020767, -0.7930195565417275, 0.8686160100637714, -0.2248634028775609, 0.07339061556092075, 0.22759818218267802, -0.8348795941690472, -0.7910211326264229, -0.9040007703012096, 0.09002893235021525, 0.5513354875526857, 0.8264625198495734, -0.38639672910223255, 0.8883850241389724, -0.43439143794810375, -0.29940807310297246, -0.21516823465631973, -0.07182049444996541, -0.6271427138928527, 0.998408534429952, -0.24861241162933023, 0.24837010699066742, 0.5777705187951017, 0.3609778344414969, -0.5710310251790385, 0.2601826464186747, 0.6264011691203892, -0.7568705578436004, -0.1779568019478781, 0.3953201137883311, 0.7589682904916071, 0.26191166973604174, 0.6109002701138739, 0.05543041986726527, 0.38086103793447834, -0.1641420216610907, -0.685403114257042, 0.016157524529266887, -0.44187475749919236, -0.23935617227312234, 0.5451295375455822, 0.9949829333076181, -0.3180530172880436, -0.5705361096410566, 0.7540608008567613, -0.22549962126746093, 0.8413117539201991, -0.8514233003111304, 0.9678828315488319, -0.055425924799840764, 0.01009849619007741, 0.9122624452845938, 0.28228545484314194, 0.6597979039122528, 0.7791267418024779, 0.26348867447292035, 0.8957343326904343, 0.41144660831833746, 0.1276837763539158, 0.414822637028718, 0.8978889095530647, -0.4334681362856141, 0.04882480106786513)
        private val actionConstrainedANNControllerWeights = doubleArrayOf(-0.5929226329740656, -0.9796378102379124, 0.30156885793298494, 0.879130929705884, -0.13313668437056747, -0.5300707670369948, -0.9047805798834636, -0.49920593896952936, 0.20948409955100966, -0.6236188538821485, 0.14960282570579664, 0.5405594702735821, 0.09139228379321751, -0.6227636636413851, 0.11823337598867689, 0.05138102022493518, -0.2992868662739665, -0.3267471755691367, -0.6897728287730238, 0.4783215146524109, -0.2077191247203729, -0.9084797835285776, 0.25287503990452787, 0.1244788165957118, -0.9188615125981576, -0.48266136407315385, 0.9303355349124094, -0.8168886917072682, 0.046420585074953946, 0.3625336675064601, -0.8454796660139601, 0.1948697398064443, -0.03103758520680988, -0.13159827721028883, 0.097401357880885, 0.5580758632532188, -0.8706832484712892, -0.999268054920704, -0.9105665257082944, 0.6185831677620939, -0.5874214735674903, -0.4160460133594157, 0.4711390562848663, -0.9323088284689631, 0.2204499411439378, 0.844561878399551, 0.208290360359765, 0.7226135318681499, 0.4505920536722996, 0.5058062433536026, 0.3491284306640443, 0.6666745217315904, -0.9011135528766725, -0.973391800420977, 0.9711699574308332, 0.7638259982114626, 0.3080507047062786, 0.6757416652856303, 0.5953509235440324, 0.3004468920730854, 0.47949504092257533, -0.40090821277943145, 0.01144664435317222, 0.4526266771551646, -0.4086135725277402, -0.033167597741829535, -0.4828119227709655, -0.7282403815805198, -0.6158357448446157, 0.9946115549436181, 0.24847867943014146, 0.5347091421652088, 0.31015219906622327, -0.9121289536230828, -0.7719418348732305, -0.36048330143514695, 0.24801190394673944, 0.4802554843964413, 0.12749426960406307, -0.2253479837374499, 0.6099371942091159, -0.36752461961038074, -0.1403048124162527, 0.7876431711031333, 0.3465737372767901, 0.7467659592033329, 0.2844716006739656, -0.5718049900158133, 0.03794179555720456, -0.12997571117164086, -0.061176702296956265, 0.6780195076946758, -0.06534123723292162, 0.07175733552538888, -0.2749875592036968, 0.12479844780042804, -0.009079161281276393, -0.19270311011689079, -0.03817741777361294, 0.7887956865172312, -0.1288260098281926, -0.31823160929621097, -0.09764271914105338, 0.6541602215330569, 0.04698135020033001, -0.10771299713158777, -0.24753290093835045, 0.5819178682711275, 0.1969122150211544, -0.06439830505798794)
        private val currentBestANNControllerWeights = doubleArrayOf(-0.2595589315133693, 0.6900942614898196, -0.2028562515385297, -0.11867438190939894, -0.21118525350075945, 0.6925602744075408, 0.09432291682881888, 0.796221324077717, 0.23153281681620208, 0.34615573233154917, 0.5809554136973918, -0.8780503170818865, 0.02479539136214437, 0.03500725658131709, 0.4068831177147081, 0.44135640919864016, -0.8283927691830226, 0.6794475460073937, 0.7718653794387933, 0.5266780221226888, -0.6121088223922182, -0.3607531963186752, -0.24422037075598402, 0.9579461253787644, -0.13996091875147054, -0.15742378579267835, -0.22631502362736433, -0.6818319127415213, 0.5191989697826687, 0.45601680904329167, -0.020585029212016437, -0.04233164456450744, 0.6274126780925255, 0.8737018040525795, 0.17269463298444587, 0.42196146900367815, -0.47890249638683224, 0.3199155673401428, -0.3469063284829901, 0.49281685771709505, 0.12608754866204475, 0.7478882662395192, 0.7982091358898089, -0.49430899455416055, -0.033069437870630436, -0.7504016879475341, -0.04672555775025766, -0.4776752502481876, -0.21381792861089877, -0.7106531223928256, 0.34080832866230515, 0.08864219696047848, 0.4964274183156432, 0.025756244446135, 0.6700541449309465, -0.4140032018061546, 0.7000056569973125, 0.9064749801619292, 0.12350178342134677, 0.5363371974935605, -0.9474645528633805, -0.573432609044159, -0.8123758938756822, -0.9725363992381906, -0.04999891005007062, -0.30899734707389914, -0.5315753909087617, 0.9465038194793634, -0.6029061202104982, -0.011038247736526285, 0.34366958620179866, -0.32112306333627627, 0.24472665610850464, -0.6055455573688544, 0.24076703848926684, -0.2358537808859258, -0.14204094581009685, -0.018931270651466825, -0.13137544455977013, 0.22657695018092938, -0.688282076806396, 0.6238296247142783, -0.8692979649772361, 0.4243130145479206, 0.9352806075759204, 0.21066294643782113, 0.9160478061095039, 0.5754207559309552, -0.4090524800280282, 0.4928045780794408, -0.5227744224017732, 0.022864049263162878, -0.005233248645895028, -0.812691979291692, 0.987924136548918, 0.39203140390424585, -0.19134326480944663, 0.5046095178007006, -0.6023576151704042, 0.29621952040793675, -0.13145726410035308, -0.9644264610441615, -0.2984703335134078, 0.7953721923926749, 0.2402754962099436, -0.5657611297562615, -0.4928715593562609, 0.5984000202369957, 0.8793614824903493, -0.2526239790279394)

    }

}