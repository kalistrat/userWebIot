/**
 * Created by kalistrat on 13.11.2017.
 */
com_vaadin_Diagram  = function () {
    //var diagramElement = this.getElement();
    //var diagramFrame = d3.select(diagramElement).append("svg:svg").attr("width", 500).attr("height", 500);
    //diagramFrame.append("svg:circle").attr("cx", 250).attr("cy", 250).attr("r", 20).attr("fill", "red");
    //
    //this.onStateChange = function() {
    //    var coords = this.getState().coords;
    //    d3.selectAll("circle").transition().attr("cx", parseInt(coords[0]));
    //    d3.selectAll("circle").transition().delay(500).attr("cy", parseInt(coords[1]));
    //}

    var diagramElement = this.getElement();

    var data = JSON.parse(this.getState().coords);
    var dataType;

    if (data.length <= 1){
        data.push({date: "01.01.2000 12:01:33", value: 1, tvalue: "0"});
    }


    //for(var ii = 0; ii < data.length; ii++) {
    //    alert("data.value : " + data[ii].tvalue);
    //}


    var svg = d3.select(diagramElement).append("svg").attr("width", 640).attr("height", 500),
        margin = {top: 20, right: 20, bottom: 110, left: 120},
        margin2 = {top: 430, right: 20, bottom: 30, left: 120},
        width = +svg.attr("width") - margin.left - margin.right,
        height = +svg.attr("height") - margin.top - margin.bottom,
        height2 = +svg.attr("height") - margin2.top - margin2.bottom;

    d3.timeFormatDefaultLocale({
        "decimal": ",",
        "thousands": "\xa0",
        "grouping": [3],
        "currency": ["", " руб."],
        "dateTime": "%A, %e %B %Y г. %X",
        "date": "%d.%m.%Y",
        "time": "%H:%M:%S",
        "periods": ["ДП", "ПП"],
        "days": ["воскресенье", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота"],
        "shortDays": ["вс", "пн", "вт", "ср", "чт", "пт", "сб"],
        "months": ["январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"],
        "shortMonths": ["янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"]
    });

    var parseDate = d3.timeParse("%d.%m.%Y %H:%M:%S");

    if (data[0].tvalue == "") {
        dataType = "numeric";
    } else {
        dataType = "textual";
    }


    var x = d3.scaleTime().range([0, width]),
        x2 = d3.scaleTime().range([0, width]);

    y = d3.scaleLinear().range([height, 0]),
        y2 = d3.scaleLinear().range([height2, 0]);


    var xAxis = d3.axisBottom(x),
        xAxis2 = d3.axisBottom(x2),
        yAxis = d3.axisLeft(y);


    if (dataType.valueOf() == "textual") {

        var allTicksValues = [];
        for(i=0; i<data.length; i++) {
            allTicksValues.push(data[i].value);
        }

        Array.prototype.contains = function(v) {
            for(var i = 0; i < this.length; i++) {
                if(this[i] === v) return true;
            }
            return false;
        };

        Array.prototype.unique = function() {
            var arr = [];
            for(var i = 0; i < this.length; i++) {
                if(!arr.contains(this[i])) {
                    arr.push(this[i]);
                }
            }
            return arr;
        }
        var uniquesVals = allTicksValues.unique();

        yAxis.tickValues(uniquesVals);

        yAxis.tickFormat(function(d) {
            var iTick = "";
            for(i=0; i<data.length; i++) {
                if (parseInt(data[i].value) == parseInt(d)) {
                    iTick = data[i].tvalue;
                }
            }
            return iTick;
        });
    }

    var brush = d3.brushX()
        .extent([[0, 0], [width, height2]])
        .on("brush end", brushed);

    var zoom = d3.zoom()
        .scaleExtent([1, Infinity])
        .translateExtent([[0, 0], [width, height]])
        .extent([[0, 0], [width, height]])
        .on("zoom", zoomed);

    var area = d3.area()
        .curve(d3.curveMonotoneX)
        .x(function(d) { return x(parseDate(d.date)); })
        .y0(height)
        .y1(function(d) { return y(d.value); });

    var area2 = d3.area()
        .curve(d3.curveMonotoneX)
        .x(function(d) { return x2(parseDate(d.date)); })
        .y0(height2)
        .y1(function(d) { return y2(d.value); });

    svg.append("defs").append("clipPath")
        .attr("id", "clip")
        .append("rect")
        .attr("width", width)
        .attr("height", height);

    var focus = svg.append("g")
        .attr("class", "focus")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var context = svg.append("g")
        .attr("class", "context")
        .attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");


    x.domain(d3.extent(data, function(d) { return parseDate(d.date); }));
    y.domain([d3.min(data, function(d) { return d.value; }), d3.max(data, function(d) { return d.value; })]);
    x2.domain(x.domain());
    y2.domain(y.domain());

    focus.append("path")
        .datum(data)
        .attr("class", "area")
        .attr("d", area);

    focus.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    focus.append("g")
        .attr("class", "axis axis--y")
        .call(yAxis);

    context.append("path")
        .datum(data)
        .attr("class", "area")
        .attr("d", area2);

    context.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height2 + ")")
        .call(xAxis2);

    context.append("g")
        .attr("class", "brush")
        .call(brush)
        .call(brush.move, x.range());

    svg.append("g")
        .attr("class", "grid")
        .attr("transform", "translate(120," + "20" + ")")
        .call(make_y_gridlines()
            .tickSize(-width)
            .tickFormat("")
        );

    svg.append("rect")
        .attr("class", "zoom")
        .attr("width", width)
        .attr("height", height)
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .call(zoom);

    function brushed() {
        if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
        var s = d3.event.selection || x2.range();
        x.domain(s.map(x2.invert, x2));
        focus.select(".area").attr("d", area);
        focus.select(".axis--x").call(xAxis);
        svg.select(".zoom").call(zoom.transform, d3.zoomIdentity
            .scale(width / (s[1] - s[0]))
            .translate(-s[0], 0));
    }

    function zoomed() {
        if (d3.event.sourceEvent && d3.event.sourceEvent.type === "brush") return; // ignore zoom-by-brush
        var t = d3.event.transform;
        x.domain(t.rescaleX(x2).domain());
        focus.select(".area").attr("d", area);
        focus.select(".axis--x").call(xAxis);
        context.select(".brush").call(brush.move, x.range().map(t.invertX, t));
    }

    function type(d) {
        d.date = parseDate(d.date);
        d.value = +d.value;
        return d;
    }

    // gridlines in y axis function
    function make_y_gridlines() {
        return yAxis.ticks(5)
    }
}
