Vue.component('discoverytable-grid', {
    template: '#grid-template',
    props: {
        data: Array,
        columns: Array,
        filterKey: String
    },
    data: function () {
        var sortOrders = {}
        this.columns.forEach(function (key) {
            sortOrders[key] = 1
        })
        return {
            sortKey: '',
            sortOrders: sortOrders
        }
    },
    computed: {
        filteredData: function () {
            var sortKey = this.sortKey
            var filterKey = this.filterKey && this.filterKey.toLowerCase()
            var order = this.sortOrders[sortKey] || 1
            var data = this.data
            if (filterKey) {
                data = data.slice(this.startRow * this.rowsPerPage, this.rowsPerPage).filter(function (row) {
                    return Object.keys(row).some(function (key) {
                        return String(row[key]).toLowerCase().indexOf(filterKey) > -1
                    })
                })
            }
            if (sortKey) {
                data = data.slice().sort(function (a, b) {
                    a = a[sortKey]
                    b = b[sortKey]
                    return (a === b ? 0 : a > b ? 1 : -1) * order
                })
            }
            return data
        }
    },
    filters: {
        capitalize: function (str) {
            return str.charAt(0).toUpperCase() + str.slice(1)
        }
    },
    methods: {
        sortBy: function (key) {
            this.sortKey = key
            this.sortOrders[key] = this.sortOrders[key] * -1
        }
    }
})

// bootstrap the discoverytable
function fetchData() {
    var self = this;
    self.loading = true;
    var offset = new Date().getTimezoneOffset();
    axios.get('api/discovery/table', {
        params: {
            timeZone: offset
        },
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(function (response) {
            var lst = []

            for (var key in response.data) {
                var temp = [];
                temp = response.data[key];
                if (temp.action != "") {
                    temp.action = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')) + temp.action;
                }
                if (temp.fileName != "") {
                    temp.fileName = temp.fileName.substr(temp.fileName.indexOf(".") + 1);
                }
                lst.push(temp);
            }
            self.gridData = lst;
        })
        .catch(function (error) {
            console.log(error.message);
            self.loading = false;
        });
}
var discoverytable = new Vue({
    el: '#discoverytable',
    data: {
        searchQuery: '',
        gridColumns: ['fileName', 'type', 'uploadDate', 'status', 'action'],
        gridData: Array,
        startRow: 0,
        rowsPerPage: 3
    },

    methods: {
        movePages: function (amount) {
            var newStartRow = this.startRow + (amount * this.rowsPerPage);
            if (newStartRow >= 0 && newStartRow < this.gridData.length) {
                this.startRow = newStartRow;
            }
        }
    },
    mounted: function () {
        fetchData.call(this);
        setInterval(function () {
            fetchData.call(this);
        }.bind(this), 1000);
    }
})