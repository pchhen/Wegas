import React, { PropTypes } from 'react';
import { computeData, computeDiffs } from './dataCompute';
import Chartist from 'chartist';
import 'chartist/dist/chartist.min.css';
import '../../css/chartist.css';

const CHART_BAR_OPT = {
    width: 600,
    height: 400,
    axisY: {
        labelInterpolationFnc: function(v) {
            return v ? v + '%' : '<b>0%</b>';
        },
        scaleMinSpace: 20,
        onlyInteger: true
    },
    axisX: {
        offset: 50
    },
    low: 0,
    high: 100
};
const DIFF_BAR_OPT = Object.assign({}, CHART_BAR_OPT, {
    low: -100
});
const inline_style = {
    display: 'inline-block',
    whiteSpace: 'normal'
};
const noWrap = {
    whiteSpace: 'nowrap'
};
const legendStyle = {
    marginRight: 20,
    padding: '3px 10px'
};
const noDisplay = {
    display: 'none'
};
function inlineSvgStyle(node) {
    const tw = document.createTreeWalker(node, 1);
    let n;
    while ((n = tw.nextNode())) {
        n.setAttribute('style', getComputedStyle(n).cssText);
    }
}
function svgToPng(node) {
    inlineSvgStyle(node);
    return new Promise(function(resolve, reject) {
        const img = new Image();
        img.src = 'data:image/svg+xml;base64,' + btoa(window.unescape(encodeURIComponent((new XMLSerializer()).serializeToString(node))));
        img.onload = function() {
            const can = document.createElement('canvas'),
                ctx = can.getContext('2d'),
                target = new Image();
            can.width = img.width;
            can.height = img.height;
            ctx.drawImage(img, 0, 0, img.width, img.height);
            target.src = can.toDataURL();
            resolve(target);
        };
        img.onerror = reject;
    });
}
class Graph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            questionGroup: {}
        };
    //    log.info("start");
    }
    componentDidMount() {
        this.chart = new Chartist.Bar(this.refs.graph, {
            labels: [],
            series: []
        }, CHART_BAR_OPT);
        this.diffChart = new Chartist.Bar(this.refs.diffs, {
            labels: [],
            series: []
        }, DIFF_BAR_OPT);

    }
    componentWillUnmount() {
        this.chart.detach();
        this.diffChart.detach();
    }
    genAll() {
        const windowHandler = window.open();
        const {groups, snapshot, logId} = this.props;
        const tmpChart = new Chartist.Bar(this.refs.tmpChart, {
            labels: [],
            series: []
        }, CHART_BAR_OPT);
        const tmpDiff = new Chartist.Bar(this.refs.tmpDiff, {
            labels: [],
            series: []
        }, DIFF_BAR_OPT);
        let promiseChain = Promise.resolve();
        JSON.search(snapshot, '//*[@class="QuestionDescriptor"]/name').forEach(question => {
            promiseChain = promiseChain.then(() => {
                if (windowHandler.closed) {
                    throw new Error('Window has been closed, halting');
                }
                return computeData({
                    groups,
                    snapshot,
                    logId,
                    question
                }).then(data => {
                    tmpChart.update(data);
                    return data;
                }).then(computeDiffs).then(data => tmpDiff.update(data)).then(() => {
                    return Promise.all([svgToPng(tmpChart.container.firstChild), svgToPng(tmpDiff.container.firstChild)]);
                }).then(([chart, diff]) => {
                    const container = document.createElement('div');
                    const label = JSON.search(snapshot, `//*[name="${question}"]/ancestor::*[@class="ListDescriptor"]`).reduce((pre, cur) => {
                            return `${pre}${cur.label} \u2192 `;
                        }, '') + JSON.search(snapshot, `//*[@class='QuestionDescriptor'][name="${question}"]/label`)[0];
                    container.setAttribute('style', 'white-space:nowrap');
                    container.innerHTML = `<div>${label}</div>`;
                    container.appendChild(chart);
                    container.appendChild(diff);
                    windowHandler.document.body.appendChild(container);
                    return;
                }).catch(err => console.error(err));
            });
        });
        promiseChain.catch(err => {
            console.error(err);
        }).then(() => {
            tmpChart.detach();
            tmpDiff.detach();
        });
        return promiseChain;
    }
    componentDidUpdate() {
        if (!this.props.question) {
            return;
        }
        computeData(this.props).then(data => {
            this.chart.update(data);
            return data;
        }).then(computeDiffs).then(data => this.diffChart.update(data));
    }

    render() {

        const legends = this.props.groups.map((val, index) => {
            return (
                <span className={ 'color ct-series-' + String.fromCharCode(97 + index) }
                      key={ index }
                      style={ legendStyle }>{ 'Group ' + (index + 1) }</span>
                );
        });
        return (
            <div ref='box'>
              <span className='legend'>{ legends }</span>
              <div style={ noWrap }>
                <div ref='graph'
                     style={ inline_style } />
                <div ref='diffs'
                     style={ inline_style } />
              </div>
              <div ref='tmpChart'
                   style={ noDisplay } />
              <div ref='tmpDiff'
                   style={ noDisplay } />
            </div>
            );
    }
}
Graph.propTypes = {
    groups: PropTypes.arrayOf(PropTypes.array),
    question: PropTypes.string
};
Graph.defaultProps = {
    groups: []
};
export default Graph;
