import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Table } from 'antd';
import styles from '../styles.less';

@connect(({ organizationManager }) => {
  return {
    organizationManager
  };
})
class OrgList extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'organizationManager/handleGetOrgList'
    });
  }

  handleDeleteOrg = (orgId) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'organizationManager/handleDeleteOrg',
      payload: { orgId }
    });
  }

  handleUpdateOrg = (values) => {
    const { dispatch } = this.props;
    this.props.onChangeTab();
    dispatch({
      type: 'organizationManager/handleGetUpdateList',
      payload: values
    });
  }

  render() {
    const { organizationManager } = this.props;
    const { orgList, loading } = organizationManager;

    orgList && orgList.map((item, i) => {
      return item.key = i
    })

    const orgListCol = [{
      title: '组织名称',
      dataIndex: 'name'
    }, {
      title: '组织MSPID',
      dataIndex: 'mspId',
      render: (item) => (<span>{!item ? "空" : item}</span>)
    }, {
      title: '组织类型',
      dataIndex: 'type',
      render: (item) => (<span>{item === 'PEER_ORGANIZATION' ? "peer组织" : "orderer组织 "}</span>)
    }, {
      title: '是否使用TLS',
      dataIndex: 'tlsEnable',
      render: (item) => (<span>{item === true ? "是" : "否"}</span>)
    }, {
      title: '操作',
      dataIndex: 'active',
      width: '130px',
      render: (text, item) => (
        <div className={styles.active}>
          <a onClick={() => this.handleUpdateOrg(item)}>更新</a>
          <a onClick={() => this.handleDeleteOrg(item.id)}>删除</a>
        </div>
      )
    }];

    return (
      <Table loading={loading} dataSource={orgList} columns={orgListCol}></Table>
    );
  }
}

const WrapOrgList = OrgList;
export default WrapOrgList;
