serverIp = "http://49.4.114.114:8888/";

cityList = {};

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}

function getClassify(part) {
    $.ajax({
        type: "POST",
        url: serverIp + "classify/study", // 后端服务器上传地址
        data: {
            part: part
        },
        success: function (rs) {
            rs = rs.data;
            $("#label").empty();
            $("#label").html('<option value="">请选择</option>');
            for(var i = 0; i < rs.length; i++) {
                $("#label").append('<option value="' + rs[i].num + '">' + rs[i].msg + '</option>');//将列表中的内容加入到第二个下拉框
            }
            form.render("select");
        },
        error: function (returndata) {}
    });
}

//图片选择完毕
function picChooseFn() {
    isPicAvailable = false;
    var file = $("#choosePic").get(0).files[0];
    if (file === null || file === undefined) return;
    var otype = file.type || '获取失败',
        osize = file.size / 1054000;
    // 文件格式
    if ('image/jpeg' !== otype && 'image/png' !== otype && 'image/gif' !== otype && 'image/bmp' !== otype){
        $(".file-box").hide();
        $('#picInfo').show();
        $('#picInfo').text("文件格式错误 请重新选择");
        return;
    }
    // 文件大小
    if (osize.toFixed(2) > 1) {
        $('#picInfo').show();
        $('#picInfo').text(file.name + "，共" + osize.toFixed(2) + "MB。过大");
        return;
    }
    var ourl = window.URL.createObjectURL(file); //文件临时地址
    var dom = '<img id="photo" width="100%" height="100%" alt="我是image图片文件" src=' + ourl + ' title="" />';
    $(".file-box").show();
    $('.file-box').html(dom);
    $('#picInfo').hide();
    isPicAvailable = true;
    var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
    var chunkSize = 2097152, // read in chunks of 2MB
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        frOnload = function(e){
            //  log.innerHTML+="\nread chunk number "+parseInt(currentChunk+1)+" of "+chunks;
            spark.append(e.target.result); // append array buffer
            currentChunk++;
            if (currentChunk < chunks)
                loadNext();
            else{
                var md5 = spark.end();
                console.log("\n加载结束 :\n\n计算后的文件md5:\n"+md5);
                picFormData.set("md5", md5);
                picFormData.set("pic", file);
            }
        },
        frOnerror = function () {
            console.log("糟糕，好像哪里错了.");
        };
    function loadNext() {
        var fileReader = new FileReader();
        fileReader.onload = frOnload;
        fileReader.onerror = frOnerror;
        var start = currentChunk * chunkSize,
            end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    };
    loadNext();
}

//重置
function dealReset() {
    location.reload();
}

//删除
function dealDelList(checkStatus, part) {
    var checkList = checkStatus.data;
    if (checkList.length <= 0) {
        layer.alert("至少要选择一行记录");
        return;
    }
    var idList = [];
    for (var i = 0; i < checkList.length; i++) {
        idList.push(checkList[i].id);
    }
    $.ajax({
        type: "POST",
        url: serverIp + "delete/study", // 后端服务器上传地址
        data: {
            part: part,
            list: JSON.stringify(idList),
        },
        success: function (rs) {
            layer.msg("已删除");
            table.reload('demo', {
                page: {curr: 1}
            });
        },
        error: function () {
            layer.msg("删除失败");
        }
    })
}

//查找
function search() {
    var key = document.getElementById("key").value;
    if (key === undefined || key === null || key.toString().trim().length <= 0) {
        layer.msg("请输入查询条件");
    } else {
        layer.msg("查询" + key);
        table.reload('demo', {
            where: {
                key: key
            },
            page: {curr: 1}
        });
        document.getElementById("key").value = key;
    }
}

//实时监听输入框
function dealInput(){
    var key = $("#key").val();
    if(key === ''){
        table.reload('demo', {
            where: {
                key: null
            },
            page: {curr: 1}
        });
    }
}

cityList['北京市'] = ['北京市'];
cityList['天津市'] = ['天津市'];
cityList['上海市'] = ['上海市'];
cityList['重庆市'] = ['重庆市'];

cityList['河北省'] = ['石家庄市', '唐山市', '邯郸市', '秦皇岛市', '保定市', '张家口市', '承德市', '廊坊市', '沧州市', '衡水市', '邢台市', '辛集市', '藁城市', '晋州市', '新乐市', '鹿泉市', '遵化市', '迁安市', '武安市', '南宫市', '沙河市', '涿州市', '定州市', '安国市', '高碑店市', '泊头市', '任丘市', '黄骅市', '河间市', '霸州市', '三河市', '冀州市', '深州市'];
cityList['山西省'] = ['太原市', '大同市', '忻州市', '阳泉市', '长治市', '晋城市', '朔州市', '晋中市', '运城市', '临汾市', '吕梁市', '古交市', '潞城市', '高平市', '介休市', '永济市', '河津市', '原平市', '侯马市', '霍州市', '孝义市', '汾阳市'];
cityList['辽宁省'] = ['沈阳市', '大连市', '鞍山市', '抚顺市', '本溪市', '丹东市', '锦州市', '营口市', '阜新市', '辽阳市', '盘锦市', '铁岭市', '朝阳市', '葫芦岛市', '新民市', '瓦房店市', '普兰市', '庄河市', '海城市', '东港市', '凤城市', '凌海市', '北镇市', '大石桥市', '盖州市', '灯塔市', '调兵山市', '开原市', '凌源市', '北票市', '兴城市'];
cityList['吉林省'] = ['长春市', '吉林市', '四平市', '辽源市', '通化市', '白山市', '松原市', '白城市', '九台市', '榆树市', '德惠市', '舒兰市', '桦甸市', '蛟河市', '磐石市', '公主岭市', '双辽市', '梅河口市', '集安市', '临江市', '大安市', '洮南市', '延吉市', '图们市', '敦化市', '龙井市', '珲春市', '和龙市'];
cityList['黑龙江省'] = ['哈尔滨市', '大庆市', '齐齐哈尔市', '佳木斯市', '鸡西市', '鹤岗市', '双鸭山市', '牡丹江市', '伊春市', '七台河市', '黑河市', '绥化市', '五常市', '双城市', '尚志市', '纳河市', '虎林市', '密山市', '铁力市', '同江市', '富锦市', '绥芬河市', '海林市', '宁安市', '穆林市', '北安市', '五大连池市', '肇东市', '海伦市', '安达市'];
cityList['江苏省'] = ['南京市', '镇江市', '常州市', '无锡市', '苏州市', '徐州市', '连云港市', '淮安市', '盐城市', '扬州市', '泰州市', '南通市', '宿迁市', '江阴市', '宜兴市', '邳州市', '新沂市', '金坛市', '溧阳市', '常熟市', '张家港市', '太仓市', '昆山市', '吴江市', '如皋市', '通州市', '海门市', '启东市', '东台市', '大丰市', '高邮市', '江都市', '仪征市', '丹阳市', '扬中市', '句容市', '泰兴市', '姜堰市', '靖江市', '兴化市'];
cityList['浙江省'] = ['杭州市', '嘉兴市', '湖州市', '宁波市', '金华市', '温州市', '丽水市', '绍兴市', '衢州市', '舟山市', '台州市', '建德市', '富阳市', '临安市', '余姚市', '慈溪市', '奉化市', '瑞安市', '乐清市', '海宁市', '平湖市', '桐乡市', '诸暨市', '上虞市', '嵊州市', '兰溪市', '义乌市', '东阳市', '永康市', '江山市', '临海市', '温岭市', '龙泉市'];
cityList['安徽省'] = ['合肥市', '蚌埠市', '芜湖市', '淮南市', '亳州市', '阜阳市', '淮北市', '宿州市', '滁州市', '安庆市', '巢湖市', '马鞍山市', '宣城市', '黄山市', '池州市', '铜陵市', '界首市', '天长市', '明光市', '桐城市', '宁国'];
cityList['福建省'] = ['福州市', '厦门市', '泉州市', '三明市', '南平市', '漳州市', '莆田市', '宁德市', '龙岩市', '福清市', '长乐市', '永安市', '石狮市', '晋江市', '南安市', '龙海市', '邵武市', '武夷山', '建瓯市', '建阳市', '漳平市', '福安市', '福鼎市'];
cityList['江西省'] = ['南昌市', '九江市', '赣州市', '吉安市', '鹰潭市', '上饶市', '萍乡市', '景德镇市', '新余市', '宜春市', '抚州市', '乐平市', '瑞昌市', '贵溪市', '瑞金市', '南康市', '井冈山市', '丰城市', '樟树市', '高安市', '德兴市'];
cityList['山东省'] = ['济南市', '青岛市', '淄博市', '枣庄市', '东营市', '烟台市', '潍坊市', '济宁市', '泰安市', '威海市', '日照市', '莱芜市', '临沂市', '德州市', '聊城市', '菏泽市', '滨州市', '章丘市', '胶南市', '胶州市', '平度市', '莱西市', '即墨市', '滕州市', '龙口市', '莱阳市', '莱州市', '招远市', '蓬莱市', '栖霞市', '海阳市', '青州市', '诸城市', '安丘市', '高密市', '昌邑市', '兖州市', '曲阜市', '邹城市', '乳山市', '文登市', '荣成市', '乐陵市', '临清市', '禹城市'];
cityList['河南省'] = ['郑州市', '洛阳市', '开封市', '漯河市', '安阳市', '新乡市', '周口市', '三门峡市', '焦作市', '平顶山市', '信阳市', '南阳市', '鹤壁市', '濮阳市', '许昌市', '商丘市', '驻马店市', '巩义市', '新郑市', '新密市', '登封市', '荥阳市', '偃师市', '汝州市', '舞钢市', '林州市', '卫辉市', '辉县市', '沁阳市', '孟州市', '禹州市', '长葛市', '义马市', '灵宝市', '邓州市', '永城市', '项城市', '济源市'];
cityList['湖北省'] = ['武汉市', '宜昌市', '襄樊市', '黄石市', '鄂州市', '随州市', '荆州市', '荆门市', '十堰市', '孝感市', '黄冈市', '咸宁市', '大冶市', '丹江口市', '洪湖市', '石首市', '松滋市', '宜都市', '当阳市', '枝江市', '老河口市', '枣阳市', '宜城市', '钟祥市', '应城市', '安陆市', '汉川市', '麻城市', '武穴市', '赤壁市', '广水市', '仙桃市', '天门市', '潜江市', '恩施市', '利川市'];
cityList['湖南省'] = ['长沙市', '株洲市', '湘潭市', '衡阳市', '岳阳市', '郴州市', '永州市', '邵阳市', '怀化市', '常德市', '益阳市', '张家界市', '娄底市', '浏阳市', '醴陵市', '湘乡市', '韶山市', '耒阳市', '常宁市', '武冈市', '临湘市', '汨罗市', '津市市', '沅江市', '资兴市', '洪江市', '冷水江市', '涟源市', '吉首市'];
cityList['广东省'] = ['广州市', '深圳市', '汕头市', '惠州市', '珠海市', '揭阳市', '佛山市', '河源市', '阳江市', '茂名市', '湛江市', '梅州市', '肇庆市', '韶关市', '潮州市', '东莞市', '中山市', '清远市', '江门市', '汕尾市', '云浮市', '增城市', '从化市', '乐昌市', '南雄市', '台山市', '开平市', '鹤山市', '恩平市', '廉江市', '雷州市 ', '吴川市', '高州市', '化州市', '高要市', '四会市', '兴宁市', '陆丰市', '阳春市', '英德市', '连州市', '普宁市', '罗定市', '信宜市'];
cityList['海南省'] = ['海口市', '三亚市', '琼海市', '文昌市', '万宁市', '五指山市', '儋州市', '东方市'];
cityList['四川省'] = ['成都市', '绵阳市', '德阳市', '广元市', '自贡市', '攀枝花市', '乐山市', '南充市', '内江市', '遂宁市', '广安市', '泸州市', '达州市', '眉山市', '宜宾市', '雅安市', '资阳市', '都江堰市', '彭州市', '邛崃市', '崇州市', '广汉市', '什邡市', '绵竹市', '江油市', '峨眉山市', '阆中市', '华蓥市', '万源市', '简阳市', '西昌市'];
cityList['贵州省'] = ['贵阳市', '六盘水市', '遵义市', '安顺市', '清镇市', '赤水市', '仁怀市', '铜仁市', '毕节市', '兴义市', '凯里市', '都匀市', '福泉市'];
cityList['云南省'] = ['昆明市', '曲靖市', '玉溪市', '保山市', '昭通市', '丽江市', '普洱市', '临沧市', '安宁市', '宣威市', '个旧市', '开远市', '景洪市', '楚雄市', '大理市', '潞西市', '瑞丽市'];
cityList['陕西省'] = ['西安市', '咸阳市', '铜川市', '延安市', '宝鸡市', '渭南市', '汉中市', '安康市', '商洛市', '榆林市', '兴平市', '韩城市', '华阴市'];
cityList['甘肃省'] = ['兰州市', '天水市', '平凉市', '酒泉市', '嘉峪关市', '金昌市', '白银市', '武威市', '张掖市', '庆阳市', '定西市', '陇南市', '玉门市', '敦煌市', '临夏市', '合作市'];
cityList['青海省'] = ['西宁市', '格尔木市', '德令哈市'];
cityList['台湾省'] = ['台北市', '台中市', '基隆市', '高雄市', '台南市', '新竹市', '嘉义市', '板桥市', '宜兰市', '竹北市', '桃园市', '苗栗市', '丰原市', '彰化市', '南投市', '太保市', '斗六市', '新营市', '凤山市', '屏东市', '台东市', '花莲市', '马公市'];
cityList['内蒙古自治区'] = ['呼和浩特市', '包头市', '乌海市', '赤峰市', '通辽市', '鄂尔多斯市', '呼伦贝尔市', '巴彦淖尔市', '乌兰察市', '霍林郭勒市','满洲里市', '牙克石市', '扎兰屯市', '根河市', '额尔古纳市', '丰镇市', '锡林浩特市', '二连浩特市', '乌兰浩特市', '阿尔山市'];
cityList['广西壮族自治区'] = ['南宁市', '柳州市', '桂林市', '梧州市', '北海市', '崇左市', '来宾市', '贺州市', '玉林市', '百色市', '河池市', '钦州市', '防城港市', '贵港市', '岑溪市', '凭祥市', '合山市', '北流市', '宜州市', '东兴市', '桂平市'];
cityList['西藏自治区'] = ['拉萨市', '日喀则市'];
cityList['宁夏回族自治区'] = ['银川市', '石嘴山市', '吴忠市', '固原市', '中卫市', '青铜峡市', '灵武市'];
cityList['新疆维吾尔自治区'] = ['乌鲁木齐市', '克拉玛依市', '石河子市', '阿拉尔市', '图木舒克市', '五家渠市','哈密市','吐鲁番市','阿克苏市','喀什市','和田市','伊宁市','塔城市','阿勒泰市','奎屯市','博乐市','昌吉市','阜康市','库尔勒市','阿图什市','乌苏市'];
cityList['香港特别行政区'] = ['香港特别行政区'];
cityList['澳门特别行政区'] = ['澳门特别行政区'];



