(function ($, window) {

    $.fn.contextMenu = function (settings) {

        return this.each(function () {

            // Open context menu
            $(this).on("contextmenu", function (e) {
                // return native menu if pressing control
                if (e.ctrlKey) return;

                if (!$tree.tree('getSelectedNode')) return;

                if (!$tree.tree('getSelectedNode').parent.parent || !$tree.tree('getSelectedNode').parent.parent.parent) {
                    $(".edit").addClass('disabled-pointer');
                    $(".remove").addClass('disabled-pointer');

                } else {
                    $(".edit").removeClass('disabled-pointer');
                    $(".remove").removeClass('disabled-pointer');

                }
                if (!$tree.tree('getSelectedNode').parent.parent) {
                    $(".add").addClass('disabled-pointer');
                } else {
                    $(".add").removeClass('disabled-pointer');
                }
                //open menu
                var $menu = $(settings.menuSelector)
                    .data("invokedOn", $(e.target))
                    .show()
                    .css({
                        position: "absolute",
                        left: getMenuPosition(e.clientX, 'width', 'scrollLeft'),
                        top: getMenuPosition(e.clientY, 'height', 'scrollTop')
                    })
                    .off('click')
                    .on('click', 'a', function (e) {
                        $menu.hide();
                        var $invokedOn = $menu.data("invokedOn");
                        var $selectedMenu = $(e.target);
                    });

                return false;
            });

            //make sure menu closes on any click
            $(document).click(function () {
                $(settings.menuSelector).hide();
            });
        });

        function getMenuPosition(mouse, direction, scrollDir) {
            var win = $(window)[direction](),
                scroll = $(window)[scrollDir](),
                menu = $(settings.menuSelector)[direction](),
                position = mouse + scroll;

            // opening menu would pass the side of the page
            if (mouse + menu > win && menu < mouse)
                position -= menu;

            return position;
        }

    };
})(jQuery, window);


var $tree;

$(document).ready(function () {
    $tree = $('#menu');
    var data = jQuery.parseJSON($("#menuData").val());
    $tree.tree({
        data: data,
        autoOpen: 1,
        slide: true,
        dragAndDrop: true,
        onCanMove: function (node) {
            if (!node.parent.parent || !node.parent.parent.parent) {
                return false;
            } else {
                return true;
            }
        },
        onCanMoveTo: function (moved_node, target_node, position) {
            if (!target_node.parent.parent || (!target_node.parent.parent.parent && position != 'inside')) {
                return false;
            }
            if (target_node.is_menu) {
                return (position == 'inside');
            } else {
                return true;
            }
        }

    });

    $tree.bind(
        'tree.select',
        function (event) {
            if (event.node && event.node.parent.parent) {
                $(".edit").click();
                $("#page").change();
            } else {
                addNewClose();
            }
        }
    );

    $(".add").on('click', function (e) {
            e.preventDefault();
            var node_id = $tree.tree('getSelectedNode').id;
            $('#nodeId').val(node_id);
            $('#type').val('add');
            addNewShow();
        }
    );

    $(".edit").on('click', function (e) {
            e.preventDefault();
            var node_id = $tree.tree('getSelectedNode').id;
            $('#nodeId').val(node_id);
            $('#type').val('edit');
            editShow();
        }
    );

    $(".remove").on('click', function (e) {
            e.preventDefault();
            if (confirm("Confirm remove node?")) {
                var node_id = $tree.tree('getSelectedNode').id;
                var node = $tree.tree('getNodeById', node_id);
                $tree.tree('removeNode', node);
                addNewClose();
                var type = $("#type").val();
                sendSaveMenuItem(node, type);
            }
        }
    );

    $("#closeItem").on('click', function () {
        addNewClose();
    });

    $("#saveItem").on('click', function () {
        var type = $("#type").val();
        var node;
        if ($("#title")[0].validity.valid) {
            $('label[for="title"]').text("Menu item title:")
            if (type == 'add') {
                node = saveNewItem();
            } else {
                node = editItem();
            }
            sendSaveMenuItem(node, type);
        } else {
            $('label[for="title"]').append("<span style='color: red;font-style: italic; font-size: 15px;'> title can't be empty</span>");
        }
    });

    $(".tree").contextMenu({
        menuSelector: "#contextMenu"
    });

    $(".btn-submit").on('click', function (e) {
        e.preventDefault();
        $("#data").val($tree.tree('toJson'));
        this.form.submit();
    });

    $("#page").on('change', function () {
        var node_id = $("#nodeId").val();
        var node = $tree.tree('getNodeById', node_id);
        if ($("#page").val() != "") {
            $("#url").val("/page/" + $("#page").find(":selected").attr("data").replace(/ /g, '-'));
            $("#url").addClass('disabled-pointer');
        } else {
            $("#url").val(node.url);
            $("#url").removeClass('disabled-pointer');
        }
    });

});

function saveNewItem() {
    var node = {
        id: guid(),
        title: $("#title").val(),
        url: $("#url").val(),
        pageId: $("#page").val(),
        label: $("#title").val(),
        children: []
    };
    addChild(node);
    return node;
}

function editItem() {
    var node = $tree.tree('getNodeById', $("#nodeId").val());
    var old_node = {
        id: guid(),
        title: node.title,
        url: node.url,
        pageId: node.pageId,
        label: node.title,
        children: []
    };
    $tree.tree(
        'updateNode',
        node,
        {
            title: $("#title").val(),
            url: $("#url").val(),
            pageId: $("#page").val(),
            label: $("#title").val()
        }
    );
    return old_node;
}

function rollBackItem(old_node) {
    var node = $tree.tree('getNodeById', $("#nodeId").val());
    $tree.tree(
        'updateNode',
        node,
        {
            title: old_node.title,
            url: old_node.url,
            pageId: old_node.pageId,
            label: old_node.title
        }
    );
    $("#title").val(old_node.title);
    $("#url").val(old_node.url);
    $("#page").val(old_node.pageId);
}

function addChild(newNode) {
    var node_id = $("#nodeId").val();
    var parent_node = $tree.tree('getNodeById', node_id);
    $tree.tree(
        'appendNode',
        newNode,
        parent_node
    );
}

function editShow() {
    $("#type-text").html("Edit");
    var node_id = $("#nodeId").val();
    var node = $tree.tree('getNodeById', node_id);
    $("#title").val(node.title);
    if (node.getLevel() > 2) {
        $("#urlBlock").show();
        $("#url").val(node.url);
        $("#page").val(node.pageId);
    } else {
        $("#urlBlock").hide();
    }
    $("#add-new").show();
}

function addNewShow() {
    $("#type-text").html("Add");
    $("#title").val("");
    var node_id = $("#nodeId").val();
    var node = $tree.tree('getNodeById', node_id);
    if (node.getLevel() > 1) {
        $("#urlBlock").show();
        $("#url").val("");
        $("#page").val("");
    } else {
        $("#urlBlock").hide();
    }
    $("#add-new").show("");
    $("#url").removeClass('disabled-pointer');
}

function addNewClose() {
    $("#add-new").hide();
}

function sendSaveMenuItem(node, type) {
    clearErrors();

    $.post(context + "cms/menu/build/" + $("#id"), "data=" + $tree.tree('toJson'), function (data) {
        if (data.status == 'success') {
            successMsg();
        } else {
            if (type == 'add') {
                node = $tree.tree('getNodeById', node.id);
                $tree.tree('removeNode', node);
            } else {
                rollBackItem(node);
            }
            printErrors(data.errors);
        }
    }).fail(function () {
        $tree.tree('removeNode', node);
        printError(null, defErrMsg);
    });
}

function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

function clearErrors() {
    $("#errors").html("");
    $("#success").html("");
}

function successMsg() {
    $("#success").append('<div id="alert-success"> \
    <div class="alert alert-success alert-dismissable">\
        <button type="button" class="close" data-dismiss="alert"\
        aria-hidden="true">\
        &times;\
    </button>\
        Success! Well done its submitted.\
        </div>\
        </div>'
    );
}

function printErrors(errors) {
    var allErrors = '';
    $.each(errors, function (key, value) {
        if (value.defaultMessage) {
            allErrors = allErrors + value.defaultMessage + "<br/>";
        }
    });
    printError(allErrors, defErrMsg);
}