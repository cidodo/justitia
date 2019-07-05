import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'dva';
import { Icon, Tabs, Table, Button, Form, Input, List } from 'antd';
import PageHeaderLayout from '@/components/PageHeaderWrapper';
import TabPaneCon from '@/components/TabPaneCon';
import { downloadFile } from '@/utils/utils';
import OperationChannel from './operationChannel';
import AddOrgToChannel from './addOrgToChannel';
import CreateChannel from './createChannel';
import JoinChannel from './joinChannel';
import styles from '../style.less';

import org from '@/assets/组织.png';

const { TabPane } = Tabs;
const FormItem = Form.Item;
const ListItem = List.Item;
const Meta = List.Item.Meta;

const host = process.env.NODE_ENV === "production" ? '/api' : `http://${window.hostIp}`;

@connect(({ ChannelManager, network }) => {
  return {
    ChannelManager,
    network
  };
})
class wrapperChannelManger extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: '1',
      title: '',
      type: '',
      params: '',
      isShow: false,
    };
    this.downloadEle = React.createRef();
  }

  componentDidMount() {
    const { dispatch } = this.props;
    // 获取通道信息列表
    dispatch({
      type: 'ChannelManager/handleGetChannelList'
    });
    // dispatch({
    //   type: 'network/getConfigPeer',
    // });
  }

  componentDidUpdate(prevProps, prevState) {
    const { isClose } = this.props.ChannelManager;
    const { isShow } = this.state;
    if (isShow && prevState.isShow && isClose) {
      this.setState({
        isShow: false
      });
    }
  }
  // 切换tabs
  handleChangeTabs = (key) => {
    this.setState({
      activeKey: key === "1" ? "1" : "2"
    });
  }
  // 打开弹窗
  handleShowModal = (type, title, params) => {
    this.setState({
      isShow: true,
      title,
      type,
      params,
    });
  }
  // 关闭弹窗
  handleCloseModal = () => {
    this.setState({
      isShow: false,
    });
  }
  // 添加通道
  onAddChannel = (e) => {
    const { dispatch } = this.props;
    const value = e.target.value;
    if (value !== "") {
      dispatch({
        type: 'ChannelManager/handleAddChannel',
        payload: { channelId: value }
      });
    }
  }
  // 下载通道组织信息
  handleDownLoadConfig = () => {
    const params = {
      ele: this.downloadEle.current,
      fileName: '通道组织配置文件.json',
      url: `${host}/channel/organization/config`,
      type: 'get'
    }
    downloadFile(params);
  }

  render() {
    const DetailInfo = (
      <div className={styles.peer}>通道信息管理</div>
    );
    // 下载文件按钮
    const DownLoadBtn = (
      <Button className={styles.downloadBtn} type="primary" onClick={this.handleDownLoadConfig}>
        下载通道组织配置文件
      </Button>
    );
    // table格式
    const columns = [
      { title: '通道名称', key: 'channelId', dataIndex: 'channelId', width: '15%' },
      {
        title: 'peer节点', key: 'peers', dataIndex: 'peers', render: (peersList) => (
          <span>
            {peersList.map((ele, index) => (
              <span key={index}>{ele}，</span>
            ))}
          </span>
        )
      },
      {
        title: '组织名称', key: 'orgs', dataIndex: 'orgs', render: (orgsList) => (
          <span>
            {orgsList.map((ele, index) => (
              <span key={index}>{ele.name}，</span>
            ))}
          </span>
        )
      },
      {
        title: '操作', key: 'action', width: '100px', render: (params) => (
          <p className={styles.tableAction}>
            {/* <a onClick={() => this.handleShowModal('peer', '添加节点', params.channelId)}>添加节点</a>
            <a onClick={() => this.handleShowModal('addOrg', '添加组织', params.channelId)}>添加组织</a> */}
            <a onClick={() => this.handleShowModal('delOrg', '删除组织', params.channelId)}>删除组织</a>
          </p>
        )
      },
    ];
    const { activeKey, isShow, title, type, params } = this.state;
    const { form, ChannelManager, network } = this.props;
    const { loading, createChannel, getChannelList } = ChannelManager;
    const { isFetching } = createChannel;
    const { peerConfig } = network;

    return (
      <PageHeaderLayout detailInfo={DetailInfo} logo={org}>
        <Tabs onChange={(key) => this.handleChangeTabs(key)} activeKey={activeKey} className={styles.tabs}>
          <TabPane
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />通道列表</span>}
            key="1"
          >
            <TabPaneCon title="通道列表" select={DownLoadBtn}>
              <Table rowKey="channelId" bordered loading={loading} columns={columns} dataSource={getChannelList}
                expandedRowRender={record =>
                  <List
                    size="small"
                    header={<div>通道内组织详情</div>}
                    dataSource={record.orgs}
                    renderItem={item => (
                      <ListItem>
                        <Meta
                          title={item.msp}
                          description={`锚节点：${item.anchorPeers ? item.anchorPeers : '""'}`}
                        />
                      </ListItem>
                    )}
                  />
                }
              />
              <OperationChannel title={title} type={type} params={params} isShow={isShow} onCloseModal={() => this.handleCloseModal()} />
              <div ref={this.downloadEle}></div>
            </TabPaneCon>
            <TabPaneCon title="创建通道">
              <CreateChannel />
            </TabPaneCon>
            <TabPaneCon title="加入通道">
              <JoinChannel />
            </TabPaneCon>
          </TabPane>
          <TabPane
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />添加组织</span>}
            key="2"
          >
            <TabPaneCon title="添加组织" children={<AddOrgToChannel dataChannel={getChannelList} />} />
          </TabPane>
        </Tabs>
      </PageHeaderLayout>
    );
  }
}

export default wrapperChannelManger;
