serverIp = "http://192.168.0.103:8888/";
nginxIp = "http://192.168.0.103:8000/resource/";
cityList = {};

function testTitle(part, title) {
    $.when(
        $.ajax({
            type: "POST",
            url: serverIp + "test/study", // 后端服务器上传地址
            async:false,//同步
            data: {
                part:part,
                title:title
            },
            success: function (rs) {},
            error: function () {}
        })
    ).then(function (rs) {
        console.log("返回的" + rs);
        return rs;
    }, function (reason) {
        return false;
    });
    console.log("返回");
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
    console.log(ourl);
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

function upLoadPic(formData) {
    $.ajax({
        type: "POST",
        url: serverIp + "upload/pic", // 后端服务器上传地址
        data: formData,
        cache: false, // 是否缓存
        async:false,//同步 是否异步执行
        processData: false, // 是否处理发送的数据  (必须false才会避开jQuery对 formdata 的默认处理)
        contentType: false, // 是否设置Content-Type请求头
        success: function (rs) {
            if(rs.num === 1){
                return null;
            }
            return rs.data;
        },
        error: function () {
            return null;
        }
    })
}

function upLoadBook(formData) {
    $.ajax({
        type: "POST",
        url: serverIp + "upload/book", // 后端服务器上传地址
        data: formData,
        cache: false, // 是否缓存
        async:false,//同步 是否异步执行
        processData: false, // 是否处理发送的数据  (必须false才会避开jQuery对 formdata 的默认处理)
        contentType: false, // 是否设置Content-Type请求头
        success: function (rs) {
            if(rs.num === 1){
                return null;
            }
            return rs.data;
        },
        error: function () {
            return null;
        }
    })
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
    deleteStudy(part, idList);
}

function deleteStudy(part, idList) {
    $.ajax({
        type: "POST",
        url: serverIp + "delete/study", // 后端服务器上传地址
        data: {
            part: part,
            list: JSON.stringify(idList),
        },
        success: function (rs) {
            layer.msg("已删除");
            //重新渲染
            var page = $(".layui-laypage-em").next().html();
            table.reload('demo', {
                page: {curr: page}
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

cityList['河北省'] = ['石家庄', '唐山', '邯郸', '秦皇岛', '保定', '张家口', '承德', '廊坊', '沧州', '衡水', '邢台', ''];
cityList['山西省'] = ['太原', '大同', '忻州', '阳泉', '长治', '晋城', '朔州', '晋中', '运城', '临汾', '吕梁'];
cityList['辽宁省'] = ['沈阳', '大连', '鞍山', '抚顺', '本溪', '丹东', '锦州', '营口', '阜新', '辽阳', '盘锦', '铁岭', '朝阳', '葫芦岛'];
cityList['吉林省'] = ['长春', '吉林', '四平', '辽源', '通化', '白山', '松原', '白城'];
cityList['黑龙江省'] = ['哈尔滨', '大庆', '齐齐哈尔', '佳木斯', '鸡西', '鹤岗', '双鸭山', '牡丹江', '伊春', '七台河', '黑河', '绥化'];
cityList['江苏省'] = ['南京', '镇江', '常州', '无锡', '苏州', '徐州', '连云港', '淮安', '盐城', '扬州', '泰州', '南通', '宿迁'];
cityList['浙江省'] = ['杭州', '嘉兴', '湖州', '宁波', '金华', '温州', '丽水', '绍兴', '衢州', '舟山', '台州'];
cityList['安徽省'] = ['合肥', '蚌埠', '芜湖', '淮南', '亳州', '阜阳', '淮北', '宿州', '滁州', '安庆', '巢湖', '马鞍山', '宣城', '黄山', '池州', '铜陵'];
cityList['福建省'] = ['福州', '厦门', '泉州', '三明', '南平', '漳州', '莆田', '宁德', '龙岩'];
cityList['江西省'] = ['南昌', '九江', '赣州', '吉安', '鹰潭', '上饶', '萍乡', '景德镇', '新余', '宜春', '抚州'];
cityList['山东省'] = ['济南', '青岛', '淄博', '枣庄', '东营', '烟台', '潍坊', '济宁', '泰安', '威海', '日照', '莱芜', '临沂', '德州', '聊城', '菏泽', '滨州'];
cityList['河南省'] = ['郑州', '洛阳', '开封', '漯河', '安阳', '新乡', '周口', '三门峡', '焦作', '平顶山', '信阳', '南阳', '鹤壁', '濮阳', '许昌', '商丘', '驻马店'];
cityList['湖北省'] = ['武汉', '宜昌', '襄樊', '黄石', '鄂州', '随州', '荆州', '荆门', '十堰', '孝感', '黄冈', '咸宁'];
cityList['湖南省'] = ['长沙', '株洲', '湘潭', '衡阳', '岳阳', '郴州', '永州', '邵阳', '怀化', '常德', '益阳', '张家界', '娄底'];
cityList['广东省'] = ['广州', '深圳', '汕头', '惠州', '珠海', '揭阳', '佛山', '河源', '阳江', '茂名', '湛江', '梅州', '肇庆', '韶关', '潮州', '东莞', '中山', '清远', '江门', '汕尾', '云浮'];
cityList['海南省'] = ['海口', '三亚'];
cityList['四川省'] = ['成都', '绵阳', '德阳', '广元', '自贡', '攀枝花', '乐山', '南充', '内江', '遂宁', '广安', '泸州', '达州', '眉山', '宜宾', '雅安', '资阳'];
cityList['贵州省'] = ['贵阳', '六盘水', '遵义', '安顺'];
cityList['云南省'] = ['昆明', '曲靖', '玉溪', '保山', '昭通', '丽江', '普洱', '临沧'];
cityList['陕西省'] = ['西安', '咸阳', '铜川', '延安', '宝鸡', '渭南', '汉中', '安康', '商洛', '榆林'];
cityList['甘肃省'] = ['兰州', '天水', '平凉', '酒泉', '嘉峪关', '金昌', '白银', '武威', '张掖', '庆阳', '定西', '陇南'];
cityList['青海省'] = ['西宁'];
cityList['台湾省'] = ['台北', '台中', '基隆', '高雄', '台南', '新竹', '嘉义'];
cityList['内蒙古自治区'] = ['呼和浩特', '包头', '乌海', '赤峰', '通辽', '鄂尔多斯', '呼伦贝尔', '巴彦淖尔', '乌兰察布'];
cityList['广西壮族自治区'] = ['南宁', '柳州', '桂林', '梧州', '北海', '崇左', '来宾', '贺州', '玉林', '百色', '河池', '钦州', '防城港', '贵港'];
cityList['西藏自治区'] = ['拉萨'];
cityList['宁夏回族自治区'] = ['银川', '石嘴山', '吴忠', '固原', '中卫'];
cityList['新疆维吾尔自治区'] = ['乌鲁木齐', '克拉玛依'];
cityList['香港特别行政区'] = ['香港特别行政区'];
cityList['澳门特别行政区'] = ['澳门特别行政区'];



