/**
 * Created by Viki on 2017/5/30.
 */

//function theme_switch(){
//    var color = $("#theme_switch").value();
//    alert(color);
//}

function rgb2hex(rgb) {
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    function hex(x) {
        return ("0" + parseInt(x).toString(16)).slice(-2);
    }
    return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
}
$("#theme_switch").bind("click", function () {
    var colors = ["#FAF9DE", "#FFFFFF", "#FFF2E2", "#FDE6E0", "#E3EDCD", "#DCE2F1", "#E9EBFE", "#EAEAEF"];
    var color = rgb2hex($("body").css("background-color")).toUpperCase();
    var new_color = colors[0];
    for (var i = 0; i < colors.length; i++) {
        if (color == colors[i].toUpperCase()) {
            if (i >= colors.length - 1) {
                new_color = colors[0];
            } else {
                new_color = colors[i + 1];
            }
        }
    }
    //alert("new Color:"+new_color);
    $("body").css("background-color", new_color);
});